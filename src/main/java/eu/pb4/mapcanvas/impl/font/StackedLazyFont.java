package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.LazyCanvasFont;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record StackedLazyFont(LazyCanvasFont[] fonts, Metadata metadata) implements LazyCanvasFont {
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

    @Override
    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public boolean isLoaded() {
        for (var x : this.fonts) {
            if (!x.isLoaded()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void requestLoad() {
        for (var x : this.fonts) {
            x.requestLoad();
        }
    }
}
