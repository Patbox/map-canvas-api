package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record SubView(DrawableCanvas source, int x1, int y1, int width, int height) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
            return this.source.getRaw(x + x1, y + y1);
        }

        return 0;
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
            this.source.setRaw(x + x1, y + y1, color);
        }
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }
}
