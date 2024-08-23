package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

public record Matrix3x2fTransformedView(DrawableCanvas source, int width, int height, Matrix3x2fc base, Vector2f ownVec) implements DrawableCanvas {
    @Override
    public byte getRaw(int x, int y) {
        this.ownVec.set(x, y);
        this.base.transformPosition(this.ownVec);
        if (this.ownVec.x < 0 || this.ownVec.x >= this.source.getWidth() || this.ownVec.y < 0 || this.ownVec.y >= this.source.getHeight()) {
            return 0;
        }

        return this.source.getRaw((int) this.ownVec.x, (int) this.ownVec.y);
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        this.ownVec.set(x, y);
        this.base.transformPosition(this.ownVec);
        if (this.ownVec.x < 0 || this.ownVec.x >= this.source.getWidth() || this.ownVec.y < 0 || this.ownVec.y >= this.source.getHeight()) {
            return;
        }

        this.source.setRaw((int) this.ownVec.x, (int) this.ownVec.y, color);
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
