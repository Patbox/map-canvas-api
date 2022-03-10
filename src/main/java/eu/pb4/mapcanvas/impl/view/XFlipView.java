package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record XFlipView(DrawableCanvas source) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        return this.source.getRaw(this.getWidth() - 1 - x, y);
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.source.setRaw(this.getWidth() - 1 - x, y, color);
    }

    @Override
    public int getHeight() {
        return this.source.getHeight();
    }

    @Override
    public int getWidth() {
        return this.source.getWidth();
    }
}
