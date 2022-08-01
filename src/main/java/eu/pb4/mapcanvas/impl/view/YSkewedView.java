package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record YSkewedView(DrawableCanvas source, double skewPerPixel) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        return this.source.getRaw(x + (int) (y * skewPerPixel), y);
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.source.setRaw(x + (int) (y * skewPerPixel), y, color);
    }

    @Override
    public int getHeight() {
        return this.source.getHeight();
    }

    @Override
    public int getWidth() {
        return this.source.getWidth() + (int) (this.getHeight() * skewPerPixel);
    }
}
