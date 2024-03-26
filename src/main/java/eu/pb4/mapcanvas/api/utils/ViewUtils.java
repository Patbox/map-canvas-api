package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.impl.view.*;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

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

    public static DrawableCanvas matrix(DrawableCanvas source, int width, int height, Matrix3x2fc matrix3x2f) {
        //return source;
        return new Matrix3x2fTransformedView(source, width, height, new Matrix3x2f(matrix3x2f).invert(), new Vector2f());
    }

    public static DrawableCanvas matrix(DrawableCanvas source, Matrix3x2fc matrix3x2f) {
        int width = 0;
        int height = 0;

        var vec = new Vector2f(0, 0);
        {
            matrix3x2f.transformPosition(vec);
            width = Math.max(width, MathHelper.ceil(vec.x));
            height = Math.max(height, MathHelper.ceil(vec.y));
        }
        {
            vec.set(0, source.getHeight());
            matrix3x2f.transformPosition(vec);
            width = Math.max(width, MathHelper.ceil(vec.x));
            height = Math.max(height, MathHelper.ceil(vec.y));
        }
        {
            vec.set(source.getWidth(), 0);
            matrix3x2f.transformPosition(vec);
            width = Math.max(width, MathHelper.ceil(vec.x));
            height = Math.max(height, MathHelper.ceil(vec.y));
        }
        {
            vec.set(source.getWidth(), source.getHeight());
            matrix3x2f.transformPosition(vec);
            width = Math.max(width, MathHelper.ceil(vec.x));
            height = Math.max(height, MathHelper.ceil(vec.y));
        }

        return new Matrix3x2fTransformedView(source, width, height, new Matrix3x2f(matrix3x2f).invert(), vec);
    }

    @FunctionalInterface
    public interface Transformer {
        Point transform(int x, int y);
        record Point(int x, int y) {}
    }

}
