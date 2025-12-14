package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

public record AwtFont(Font font, Metadata metadata, boolean antiAliasing) implements CanvasFont {
    private static final FontRenderContext CONTEXT = new FontRenderContext(null, false, false);
    private static final FontRenderContext CONTEXT_AA = new FontRenderContext(null, true, false);
    private static final float FONT_SCALE = 10 / 8f;

    @Override
    public int getTextWidth(String text, double size) {
        size *= FONT_SCALE;

        return (int) font.deriveFont((float) size).getStringBounds(text, getContext()).getWidth();
    }

    @Override
    public int getGlyphWidth(int character, double size, int offset) {
        size *= FONT_SCALE;

        return (int) (font.deriveFont((float) size).getStringBounds(Character.toString(character), getContext()).getWidth());
    }

    @Override
    public int drawGlyph(DrawableCanvas canvas, int character, int x, int y, double size, int offset, CanvasColor color) {
        size *= FONT_SCALE;
        var font = this.font.deriveFont((float) size);
        var shape = font.createGlyphVector(getContext(), Character.toChars(character));

        this.drawShape(canvas, shape, x, y, size, color);
        return (int) (shape.getLogicalBounds().getWidth());
    }

    @Override
    public void drawText(DrawableCanvas canvas, String text, int x, int y, double size, CanvasColor color) {
        size *= FONT_SCALE;
        var font = this.font.deriveFont((float) size);

        for (var line : text.split("\n")) {
            var shape = font.createGlyphVector(getContext(), line);
            this.drawShape(canvas, shape, x, y, size, color);
            y += (int) (size + 2);
        }
    }

    private void drawShape(DrawableCanvas canvas, GlyphVector shape, int x, int y, double size, CanvasColor color) {
        var outline = shape.getOutline();
        var bounds = shape.getLogicalBounds();
        var rgbColor = color.getRgbColor();
        var raw = color.getRenderColor();
        if (bounds.getWidth() == 0 || bounds.getHeight() == 0) {
            return;
        }

        var image = new BufferedImage(MathHelper.ceil(bounds.getWidth()), MathHelper.ceil(bounds.getHeight() + 2), BufferedImage.TYPE_BYTE_GRAY);
        var g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.antiAliasing ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.translate(0, size);
        g.fill(outline);
        g.dispose();

        var offset = size * (3 / 10f);
        var buf = image.getData().getDataBuffer();

        final var height = image.getHeight();
        final var width = image.getWidth();
        for (var ys = 0; ys < height; ys++) {
            var yShift = ys * width;
            var ry = (int) (ys + y - offset);
            for (var xs = 0; xs < width; xs++) {
                var val = buf.getElem(xs + yShift);
                if (val < 25) {
                    continue;
                }

                var rx = xs + x;

                if (val == 255) {
                    canvas.setRaw(rx, ry, raw);
                } else {
                    canvas.setRaw(rx, ry, CanvasUtils.findClosestRawColor(ColorHelper.lerp(val / 255f, canvas.get(rx, ry).getRgbColor(), rgbColor)));
                }
            }
        }
    }

    @Override
    public boolean containsGlyph(int character) {
        return this.font.canDisplay(character);
    }

    @Override
    public Metadata getMetadata() {
        return this.metadata;
    }

    private FontRenderContext getContext() {
        return antiAliasing ? CONTEXT_AA : CONTEXT;
    }
}
