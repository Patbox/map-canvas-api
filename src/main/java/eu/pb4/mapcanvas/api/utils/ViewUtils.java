package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.impl.view.*;

public final class ViewUtils {
    private ViewUtils() {
    }

    public static DrawableCanvas repeat(DrawableCanvas source, int width, int height) {
        return new RepeatedView(source, width, height);
    }

    public static DrawableCanvas shift(DrawableCanvas source, int deltaX, int deltaY) {
        return new ShiftedView(source, deltaX, deltaY);
    }

    public static DrawableCanvas flipX(DrawableCanvas source) {
        return new XFlipView(source);
    }

    public static DrawableCanvas flipY(DrawableCanvas source) {
        return new YFlipView(source);
    }

    public static DrawableCanvas transform(DrawableCanvas source, Transformer transformer) {
        return new TransformedView(source, transformer);
    }

    public static DrawableCanvas skewY(DrawableCanvas source, double deltaPerPixel) {
        return new YSkewedView(source, deltaPerPixel);
    }

    public static DrawableCanvas subView(DrawableCanvas source, int x1, int y1, int x2, int y2) {
        return new SubView(source, x1, y1, x2 - x1, y2 - y2);
    }

    public static DrawableCanvas rotate90Clockwise(DrawableCanvas source) {
        return new Rotate90ClockwiseView(source);
    }

    /*public static DrawableCanvas rotate(DrawableCanvas source, float angle, int translateX, int translateY) {
        //return source;
        return new RotatedView(source, angle, translateX, translateY);
    }*/

    public static DrawableCanvas rotate(DrawableCanvas source, float angle) {
        //return source;
        return new RotatedView(source, angle, source.getWidth() / 2, source.getHeight() / 2);
    }

    public static DrawableCanvas rotateDeg(DrawableCanvas source, float angle) {
        //return source;
        return rotate(source, (float) Math.toRadians(angle));
    }

    @FunctionalInterface
    public interface Transformer {
        Point transform(int x, int y);
        record Point(int x, int y) {}
    }

}
