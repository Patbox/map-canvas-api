package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.ViewUtils;
import eu.pb4.mapcanvas.impl.font.BitmapFont;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

    public <T extends CanvasFont> T register(Identifier identifier, T font) {
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

    public Collection<Identifier> fonts() {
        return this.fonts.keySet();
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
        drawText(canvas, text, x, y, size, defaultColor, CanvasColor.CLEAR);
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
     * @param shadowColor default shadow color to use
     */
    public void drawText(DrawableCanvas canvas, Text text, int x, int y, double size, CanvasColor defaultColor, CanvasColor shadowColor) {
        this.drawText(canvas, text, x, y, size, defaultColor, shadowColor, (int) (size / 8));
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
     * @param shadowColor default shadow color to use
     * @param shadowOffset offset of shadow form main text, in pixels
     */
    public void drawText(DrawableCanvas canvas, Text text, int x, int y, double size, CanvasColor defaultColor, CanvasColor shadowColor, int shadowOffset) {
        final var pixel = (int) (size / 8);
        text.asOrderedText().accept(new CharacterVisitor() {
            private int posX = 0;

            @Override
            public boolean accept(int index, Style style, int codePoint) {
                var startX = posX;
                var color = style.getColor() != null ? CanvasUtils.findClosestColor(style.getColor().getRgb()) : defaultColor;
                var shadow = style.getShadowColor() != null ? CanvasUtils.findClosestColorARGB(style.getShadowColor())
                        : shadowColor == CanvasColor.CLEAR_FORCE ?
                        CanvasUtils.findClosestColor(ColorHelper.scaleRgb(style.getColor() != null ? style.getColor().getRgb() : defaultColor.getRgbColor(), 0.25f)) : shadowColor;

                if (style.getFont() instanceof StyleSpriteSource.Font font1) {
                    var font = FontRegistry.this.getDefaultedFont(font1.id());
                    var yPos = y;

                    DrawableCanvas localCanvas;

                    if (style.isItalic()) {
                        yPos = (int) ((size / 8) * 2);
                        localCanvas = ViewUtils.skewY(ViewUtils.shift(canvas, 0, y - yPos), -4d / size);
                    } else {
                        localCanvas = canvas;
                    }

                    if (shadow != CanvasColor.CLEAR) {
                        if (style.isBold()) {
                            font.drawGlyph(localCanvas, codePoint, x + posX + 2 + shadowOffset, yPos + shadowOffset, size, 0, shadow);
                        }

                        font.drawGlyph(localCanvas, codePoint, x + posX + shadowOffset, yPos + shadowOffset, size, 0, shadow);
                    }

                    if (style.isBold()) {
                        font.drawGlyph(localCanvas, codePoint, x + posX + 2, yPos, size, 0, color);
                    }

                    posX += font.drawGlyph(localCanvas, codePoint, x + posX, yPos, size, style.isBold() ? 3 : 2, color);
                } else {
                    var glyph = style.getFont() instanceof StyleSpriteSource.Player ? BitmapFont.Glyph.PLAYER : BitmapFont.Glyph.ATLAS;
                    if (shadow != CanvasColor.CLEAR) {
                        glyph.draw(canvas,x + posX + shadowOffset, y + shadowOffset, size, 0, shadow);
                    }

                    posX += glyph.draw(canvas, x + posX, y, size, style.isBold() ? 1 : 0, CanvasColor.BLACK_NORMAL);
                }

                if (shadow != CanvasColor.CLEAR) {
                    if (style.isUnderlined()) {
                        CanvasUtils.fill(canvas, x  + startX - pixel + shadowOffset, (int) (y + pixel * 9) - pixel+ shadowOffset, x + posX+ shadowOffset, (int) (y + pixel * 9) + shadowOffset, shadow);
                    }

                    if (style.isStrikethrough()) {
                        CanvasUtils.fill(canvas , x + startX - pixel + shadowOffset, (int) (y + Math.ceil(size) / 2) + shadowOffset, x + posX + shadowOffset, (int) (y + Math.ceil(size) / 2) + pixel + shadowOffset, shadow);
                    }
                }

                if (style.isUnderlined()) {
                    CanvasUtils.fill(canvas, x  + startX - pixel, (int) (y + pixel * 9) - pixel, x + posX, (int) (y + pixel * 9), color);
                }

                if (style.isStrikethrough()) {
                    CanvasUtils.fill(canvas , x + startX - pixel, (int) (y + Math.ceil(size) / 2), x + posX, (int) (y + Math.ceil(size) / 2) + pixel, color);
                }

                return true;
            }
        });
    }

    public int getWidth(Text text, double size) {
        var tmp = new CharacterVisitor() {
            private int posX = 0;

            @Override
            public boolean accept(int index, Style style, int codePoint) {
                if (style.getFont() instanceof StyleSpriteSource.Font font1) {
                    var font = FontRegistry.this.getDefaultedFont(font1.id());
                    posX += font.getGlyphWidth( codePoint, size, style.isBold() ? 3 : 2);
                } else {
                    var glyph = style.getFont() instanceof StyleSpriteSource.Player ? BitmapFont.Glyph.PLAYER : BitmapFont.Glyph.ATLAS;
                    posX += glyph.getWidth(size, style.isBold() ? 1 : 0);
                }

                return true;
            }
        };
        text.asOrderedText().accept(tmp);
        return tmp.posX;
    }
}
