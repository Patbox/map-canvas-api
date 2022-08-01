package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.ViewUtils;

public record TransformedView(DrawableCanvas source, ViewUtils.Transformer transformer) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        var point = transformer.transform(x, y);
        return this.source.getRaw(point.x(), point.y());
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        var point = transformer.transform(x, y);
        this.source.setRaw(point.x(), point.y(), color);
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
