package eu.pb4.mapcanvas.impl.image;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

public record RawImage(int[] data, int width, int height) {
    public int get(int x, int y) {
        return this.data[x + y * width];
    }

    public int set(int x, int y, int val) {
        return this.data[x + y * width] = val;
    }

    public static RawImage of(int width, int height) {
        return new RawImage(new int[width * height], width, height);
    }

    public RawImage copy() {
        var arr = new int[this.data.length];
        System.arraycopy(this.data, 0, arr, 0, arr.length);
        return new RawImage(arr, width, height);
    }

    public void fill(int color) {
        Arrays.fill(this.data, color);
    }

    public static RawImage convert(BufferedImage image) {
        var pixels = image.getHeight() * image.getWidth();
        var out = new int[pixels];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), out, 0, image.getWidth());
        return new RawImage(out, image.getWidth(), image.getHeight());
    }

    public static RawImage convert(CanvasImage image) {
        var pixels = image.getHeight() * image.getWidth();
        var out = new int[pixels];
        for (int i = 0; i < pixels; i++) {
            out[i] = CanvasColor.getFromRaw(image.getRawAt(i)).getRgbColor();
        }
        return new RawImage(out, image.getWidth(), image.getHeight());
    }

    public static RawImage convert(DrawableCanvas image) {
        if (image instanceof CanvasImage canvasImage) {
            return convert(canvasImage);
        }

        var width = image.getWidth();
        var height = image.getHeight();
        var out = new int[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                out[x + y * width] = image.get(x, y).getRgbColor();
            }
        }

        return new RawImage(out, width, height);
    }

    public BufferedImage wrapAsImage() {
        var raster = Raster.createWritableRaster(
                new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, this.width, this.height, new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000 }),
                new DataBufferInt(this.data, this.data.length), new Point(0, 0)
        );
        return new BufferedImage(ColorModel.getRGBdefault(), raster, false, null);
    }
}
