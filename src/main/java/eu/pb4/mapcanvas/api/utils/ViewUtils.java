package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.impl.view.*;

public final class ViewUtils {
    private ViewUtils() {
    }

    public static DrawableCanvas repeat(DrawableCanvas source, int width, int height) {
        return new RepeatedView(source, width, height);
    }

    public static DrawableCanvas flipX(DrawableCanvas source) {
        return new XFlipView(source);
    }

    public static DrawableCanvas flipY(DrawableCanvas source) {
        return new YFlipView(source);
    }

    public static DrawableCanvas subView(DrawableCanvas source, int x1, int y1, int x2, int y2) {
        return new SubView(source, x1, y1, x2 - x1, y2 - y2);
    }

    public static DrawableCanvas rotate(DrawableCanvas source, float angle) {
        return new RotatedView(source, angle);
    }

    public static DrawableCanvas rotateDeg(DrawableCanvas source, float angle) {
        return new RotatedView(source, (float) Math.toRadians(angle));
    }
}
