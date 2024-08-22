package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record RepeatedView(DrawableCanvas source, int width, int height) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        if (x < this.width && y < this.height) {
            return this.source.getRaw(x % this.source.getWidth(), y % this.source.getHeight());
        }
        return 0;
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        if (x < this.width && y < this.height) {
            this.source.setRaw(x % this.source.getWidth(), y % this.source.getHeight(), color);
        }
    }

    @Override
    public void fillRaw(byte color) {
        this.source.fillRaw(color);
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
