package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record StackedFont(CanvasFont[] fonts) implements CanvasFont {

    @Override
    public int getGlyphWidth(int character, double size, int offset) {
        for (var font : fonts) {
            if (font.containsGlyph(character)) {
                return font.getGlyphWidth(character, size, offset);
            }
        }

        return BitmapFont.EMPTY.getGlyphWidth(character, size, offset);
    }

    @Override
    public int drawGlyph(DrawableCanvas canvas, int character, int x, int y, double size, int offset, CanvasColor color) {
        for (var font : fonts) {
            if (font.containsGlyph(character)) {
                return font.drawGlyph(canvas, character, x, y, size, offset, color);
            }
        }

        return BitmapFont.EMPTY.getGlyphWidth(character, size, offset);
    }

    @Override
    public boolean containsGlyph(int character) {
        for (var font : fonts) {
            if (font.containsGlyph(character)) {
                return true;
            }
        }

        return false;
    }
}
