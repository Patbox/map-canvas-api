package eu.pb4.mapcanvas.api.core;

import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.mapcanvas.impl.MultiMapCanvasImpl;
import eu.pb4.mapcanvas.impl.SingleMapCanvas;
import net.minecraft.block.MapColor;

public interface DrawableCanvas {
    default void set(int x, int y, MapColor color, MapColor.Brightness brightness) {
        this.setRaw(x, y, color.getRenderColorByte(brightness));
    }

    default void set(int x, int y, CanvasColor color) {
        this.setRaw(x, y, color.renderColor);
    }

    default CanvasColor get(int x, int y) {
        return CanvasColor.BY_RENDER_COLOR[Byte.toUnsignedInt(this.getRaw(x, y))];
    }

    default void fill(MapColor color, MapColor.Brightness brightness) {
        this.fillRaw(color.getRenderColorByte(brightness));
    }

    default void fill(CanvasColor color) {
        this.fillRaw(color.renderColor);
    }

    byte getRaw(int x, int y);

    void setRaw(int x, int y, byte color);

    default void fillRaw(byte color) {
        int width = this.getWidth();
        int height = this.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.setRaw(x, y, color);
            }
        }
    }

    int getHeight();

    int getWidth();

    default CanvasImage copy() {
        final var width = this.getWidth();
        final var height = this.getHeight();
        final var image = new CanvasImage(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRaw(x, y, this.getRaw(x, y));
            }
        }

        return image;
    }

    default CanvasImage copy(int x, int y, int width, int height) {
        final var newWidth = Math.min(this.getWidth() - x, width);
        final var newHeight = Math.min(this.getHeight() - y, height);
        final var image = new CanvasImage(newWidth, newHeight);

        for (int lx = 0; lx < newWidth; lx++) {
            for (int ly = 0; ly < newHeight; ly++) {
                image.setRaw(lx, ly, this.getRaw(x + lx, y + ly));
            }
        }

        return image;
    }

    static PlayerCanvas create() {
        return new SingleMapCanvas(MapIdManager.requestMapId());
    }

    static CombinedPlayerCanvas create(int sectionWidth, int sectionHeight) {
        return new MultiMapCanvasImpl(sectionWidth, sectionHeight);
    }

    static CanvasImage createImage(int width, int height) {
        return new CanvasImage(width, height);
    }
}
