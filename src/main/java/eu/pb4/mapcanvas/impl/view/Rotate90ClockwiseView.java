package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record Rotate90ClockwiseView(DrawableCanvas source) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        return this.source.getRaw(y, this.getWidth() - x - 1);
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.source.setRaw(y, this.getWidth() - x - 1, color);
    }

    @Override
    public int getHeight() {
        return this.source.getWidth();
    }

    @Override
    public int getWidth() {
        return this.source.getHeight();
    }
}
