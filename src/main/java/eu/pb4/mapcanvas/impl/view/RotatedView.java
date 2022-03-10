package eu.pb4.mapcanvas.impl.view;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.util.math.MathHelper;

public class RotatedView implements DrawableCanvas {
    private final int width;
    private final int height;
    private final double cos;
    private final double sin;
    private final DrawableCanvas source;
    private final int offsetX;
    private final int offsetY;
    private final int halfWidth;
    private final int halfHeight;

    public RotatedView(DrawableCanvas source, float angle) {
        this.source = source;

        this.cos = Math.cos(angle);
        this.sin = Math.sin(angle);

        int maxWidth = 0;
        int maxHeight = 0;
        int minWidth = 0;
        int minHeight = 0;
        for (var x = -source.getWidth() / 2; x < source.getWidth() / 2; x++) {
            for (var y = -source.getHeight() / 2; y < source.getHeight() / 2; y++) {
                var currentWidth = this.cos * x + this.sin * y;
                var currentHeight = -this.sin * x + this.cos * y;

                if (currentHeight > maxHeight) {
                    maxHeight = MathHelper.ceil(currentHeight);
                }

                if (currentWidth > maxWidth) {
                    maxWidth = MathHelper.ceil(currentWidth);
                }

                if (currentHeight < minHeight) {
                    minHeight = MathHelper.floor(currentHeight);
                }

                if (currentWidth < minWidth) {
                    minWidth = MathHelper.floor(currentWidth);
                }
            }
        }

        this.width = Math.max(maxWidth - minWidth, maxHeight - minHeight);
        this.height = this.width;

        this.offsetX = -source.getWidth() / 2;
        this.offsetY = -source.getHeight() / 2;
        this.halfWidth = this.width / 2;
        this.halfHeight = this.height / 2;

    }

    @Override
    public byte getRaw(int x, int y) {
        x -= this.halfWidth;
        y -= this.halfHeight;

        return this.source.getRaw(
                (int) Math.floor(this.cos * x + this.sin * y - this.offsetX),
                (int) Math.floor(-this.sin * x + this.cos * y - this.offsetY)
        );
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        x -= this.width / 2;
        y -= this.height / 2;

        this.source.setRaw(
                (int) Math.floor(this.cos * x + this.sin * y - this.offsetX),
                (int) Math.floor(-this.sin * x + this.cos * y - this.offsetY),
                color
        );
    }

    @Override
    public int getHeight() {
        return this.width;
    }

    @Override
    public int getWidth() {
        return this.height;
    }
}
