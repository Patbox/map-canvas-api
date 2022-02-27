package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public final class CanvasUtils {
    public static final int MAP_DATA_SIZE = 128;
    public static final int MAP_ICON_SIZE = 256;

    private CanvasUtils() {
    }

    public static void clear(DrawableCanvas canvas) {
        clear(canvas, CanvasColor.CLEAR);
    }

    public static void clear(DrawableCanvas canvas, CanvasColor color) {
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        final byte renderColor = color.getRenderColor();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas.setRaw(x, y, renderColor);
            }
        }
    }

    public static void fill(DrawableCanvas canvas, int x1, int y1, int x2, int y2, CanvasColor color) {
        final int minX = Math.min(x1, x2);
        final int minY = Math.min(y1, y2);
        final int maxX = Math.max(x1, x2);
        final int maxY = Math.max(y1, y2);
        final byte renderColor = color.getRenderColor();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                canvas.setRaw(x, y, renderColor);
            }
        }
    }

    public static void draw(DrawableCanvas canvas, int x, int y, DrawableCanvas source) {
        final int width = source.getWidth();
        final int height = source.getHeight();

        for (int lx = 0; lx < width; lx++) {
            for (int ly = 0; ly < height; ly++) {
                byte color = source.getRaw(lx, ly);

                if (color != 0) {
                    canvas.setRaw(lx + x, ly + y, color);
                }
            }
        }

        for (var icon : source.getIcons()) {
            canvas.createIcon(icon.getType(), icon.isVisible(), icon.getX(), icon.getY(), icon.getRotation(), icon.getText());
        }
    }

    public static void draw(DrawableCanvas canvas, int x, int y, int width, int height, DrawableCanvas source) {
        final int baseWidth = source.getWidth();
        final int baseHeight = source.getHeight();

        final double deltaX = (double) baseWidth / width;
        final double deltaY = (double) baseHeight / height;

        for (int lx = 0; lx < width; lx++) {
            for (int ly = 0; ly < height; ly++) {
                byte color = source.getRaw((int) (lx * deltaX), (int) (ly * deltaY));

                if (color != 0) {
                    canvas.setRaw(lx + x, ly + y, color);
                }
            }
        }

        for (var icon : source.getIcons()) {
            canvas.createIcon(icon.getType(), icon.isVisible(), (int) (icon.getX() * deltaX), (int) (icon.getY() * deltaY), icon.getRotation(), icon.getText());
        }
    }

    public static CanvasColor findClosestColorARGB(int argb) {
        if ((argb >> 24) == 0x00) {
            return CanvasColor.CLEAR;
        }
        return findClosestColor(argb);
    }

    public static CanvasColor findClosestColor(int rgb) {
        int shortestDistance = Integer.MAX_VALUE;
        var out = CanvasColor.CLEAR;

        int redColor = (rgb >> 16) & 0xFF;
        int greenColor = (rgb >> 8) & 0xFF;
        int blueColor = rgb & 0xFF;

        for (var canvasColor : CanvasColor.values()) {
            if (canvasColor == CanvasColor.CLEAR || canvasColor == CanvasColor.CLEAR_FORCE) {
                continue;
            }

            var tmpColor = canvasColor.getRgbColor();

            int blueCanvas = (tmpColor >> 16) & 0xFF;
            int greenCanvas = (tmpColor >> 8) & 0xFF;
            int redCanvas = tmpColor & 0xFF;

            int distance = MathHelper.square(redCanvas - redColor) + MathHelper.square(greenCanvas - greenColor) + MathHelper.square(blueCanvas - blueColor);

            if (distance < shortestDistance) {
                out = canvasColor;
                shortestDistance = distance;
            }
        }

        return out;
    }

    public static NbtCompound toNbt(DrawableCanvas canvas) {
        var nbt = new NbtCompound();
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        nbt.putString("DataType", "MapCanvasImage");
        nbt.putInt("Version", 0);
        nbt.putInt("Width", width);
        nbt.putInt("Height", height);

        var data = new byte[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data[x + y * width] = canvas.getRaw(x, y);
            }
        }

        nbt.putByteArray("Data", data);

        var iconsSource = canvas.getIcons();

        if (!iconsSource.isEmpty()) {
            var icons = new NbtList();

            for (var icon : iconsSource) {
                var iconNbt = new NbtCompound();
                iconNbt.putByte("Type", icon.getType().getId());
                iconNbt.putBoolean("Vis", icon.isVisible());
                iconNbt.putInt("X", icon.getX());
                iconNbt.putInt("Y", icon.getY());
                iconNbt.putByte("Rot", icon.getRotation());
                if (icon.getText() != null) {
                    iconNbt.putString("Text", Text.Serializer.toJson(icon.getText()));
                }

                icons.add(iconNbt);
            }

            nbt.put("Icons", icons);
        }

        return nbt;
    }
}
