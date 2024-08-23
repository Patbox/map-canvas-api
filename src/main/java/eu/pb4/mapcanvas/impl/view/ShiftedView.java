package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public record ShiftedView(DrawableCanvas source, int width, int height) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        return this.source.getRaw((x + width) % this.source.getWidth(), (y + height) % this.source.getHeight());
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.source.setRaw((x + width) % this.source.getWidth(), (y + height) % this.source.getHeight(), color);
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
