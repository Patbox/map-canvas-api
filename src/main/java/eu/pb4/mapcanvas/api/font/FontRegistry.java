package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.ViewUtils;
import eu.pb4.mapcanvas.impl.font.BitmapFont;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FontRegistry {
    private final Identifier defaultFontId;
    private final Map<Identifier, CanvasFont> fonts = new HashMap<>();
    private CanvasFont defaultFont = BitmapFont.EMPTY;

    public FontRegistry() {
        this.defaultFontId = Identifier.of("minecraft:default");
    }

    public FontRegistry(Identifier defaultFont) {
        this.defaultFontId = defaultFont;
    }

    public CanvasFont register(Identifier identifier, CanvasFont font) {
        if (this.fonts.containsKey(identifier)) {
            throw new RuntimeException("Font " + identifier + " is already registered!");
        }
        this.fonts.put(identifier, font);

        if (this.defaultFontId.equals(identifier)) {
            this.defaultFont = font;
        }

        return font;
    }

    public CanvasFont getDefaultedFont(@Nullable Identifier font) {
        return this.fonts.getOrDefault(font, this.defaultFont);
    }

    @Nullable
    public CanvasFont getFont(@Nullable Identifier font) {
        return this.fonts.get(font);
    }


    /**
     * Draws text into canvas
     *
     * @param canvas canvas to draw on
     * @param text input text
     * @param x starting x position, font will be written into right from it
     * @param y starting y position, font will be written below it
     * @param size font size, in pixels
     * @param defaultColor default color to use
     */
    public void drawText(DrawableCanvas canvas, Text text, int x, int y, double size, CanvasColor defaultColor) {
        text.asOrderedText().accept(new CharacterVisitor() {
            private int posX = 0;

            @Override
            public boolean accept(int index, Style style, int codePoint) {
                var font = FontRegistry.this.getDefaultedFont(style.getFont());

                var color = style.getColor() != null ? CanvasUtils.findClosestColor(style.getColor().getRgb()) : defaultColor;
                var startX = posX;

                var yPos = y;

                DrawableCanvas localCanvas;

                if (style.isItalic()) {
                    yPos = (int) ((size / 8) * 2);
                    localCanvas = ViewUtils.skewY(ViewUtils.shift(canvas, 0, y - yPos), -4d / size);
                } else {
                    localCanvas = canvas;
                }

                if (style.isBold()) {
                    font.drawGlyph(localCanvas, codePoint, x + posX + 2, yPos, size, 0, color);
                }

                posX += font.drawGlyph(localCanvas, codePoint, x + posX, yPos, size, style.isBold() ? 3 : 2, color);

                if (style.isUnderlined()) {
                    CanvasUtils.fill(localCanvas, x + startX, (int) (yPos + Math.ceil(size) + 2) - 2, x + posX, (int) (yPos + Math.ceil(size) + 2), color);
                }

                if (style.isStrikethrough()) {
                    CanvasUtils.fill(localCanvas, x + startX, (int) (yPos + Math.ceil(size) / 2) - 1, x + posX, (int) (yPos + Math.ceil(size) / 2) + 1, color);
                }

                return true;
            }
        });
    }
}
