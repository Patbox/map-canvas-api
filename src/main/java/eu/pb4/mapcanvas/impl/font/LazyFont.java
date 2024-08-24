package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.LazyCanvasFont;

public abstract class LazyFont implements LazyCanvasFont {
    private final Metadata fallbackMetadata;
    protected CanvasFont font;

    public CanvasFont font() {
        return this.font;
    }

    public LazyFont(Metadata metadata) {
        this.fallbackMetadata = metadata;
    }

    @Override
    public final int getTextWidth(String text, double size) {
        waitUntilLoaded();
        return this.font.getTextWidth(text, size);
    }

    @Override
    public final void drawText(DrawableCanvas canvas, String text, int x, int y, double size, CanvasColor color) {
        waitUntilLoaded();
        this.font.drawText(canvas, text, x, y, size, color);
    }

    @Override
    public final int getGlyphWidth(int character, double size, int offset) {
        waitUntilLoaded();
        return this.font.getGlyphWidth(character, size, offset);
    }

    @Override
    public final int drawGlyph(DrawableCanvas canvas, int character, int x, int y, double size, int offset, CanvasColor color) {
        waitUntilLoaded();
        return this.font.drawGlyph(canvas, character, x, y, size, offset, color);
    }

    @Override
    public final boolean containsGlyph(int character) {
        waitUntilLoaded();
        return this.font.containsGlyph(character);
    }

    @Override
    public final Metadata getMetadata() {
        if (this.font != null) {
            return this.font.getMetadata();
        }
        return this.fallbackMetadata;
    }

    protected abstract void waitUntilLoaded();

    @Override
    public boolean isLoaded() {
        return this.font != null;
    }
}
