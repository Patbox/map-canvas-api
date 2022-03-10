package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.IconContainer;
import net.minecraft.block.MapColor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.image.BufferedImage;

public final class CanvasUtils {
    private static final byte[] RGB_TO_MAP = new byte[256*256*256];

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

    /**
     * Draws source on canvas
     */
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

        if (canvas instanceof IconContainer container && source instanceof IconContainer sourceContainer) {
            for (var icon : sourceContainer.getIcons()) {
                container.createIcon(icon.getType(), icon.isVisible(), icon.getX(), icon.getY(), icon.getRotation(), icon.getText());
            }
        }
    }

    /**
     * Draws source on canvas
     */
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
        if (canvas instanceof IconContainer container && source instanceof IconContainer sourceContainer) {
            for (var icon : sourceContainer.getIcons()) {
                container.createIcon(icon.getType(), icon.isVisible(), (int) (icon.getX() * deltaX), (int) (icon.getY() * deltaY), icon.getRotation(), icon.getText());
            }
        }
    }

    /**
     * Gets closest to provided argb value canvas color
     */
    public static CanvasColor findClosestColorARGB(int argb) {
        if ((argb >> 24) == 0x00) {
            return CanvasColor.CLEAR;
        }
        return findClosestColor(argb);
    }

    /**
     * Gets closest to provided rgb value canvas color
     */
    public static CanvasColor findClosestColor(int rgb) {
        return CanvasColor.values()[Byte.toUnsignedInt(findClosestRawColor(rgb))];
    }

    /**
     * Gets closest to provided argb value canvas color (as byte/raw value)
     */
    public static byte findClosestRawColorARGB(int argb) {
        if ((argb >> 24) == 0x00) {
            return 0x00;
        }
        return findClosestRawColor(argb);
    }

    /**
     * Gets closest to provided rgb value canvas color (as byte/raw value)
     */
    public static byte findClosestRawColor(int rgb) {
        rgb = rgb & 0xFFFFFF;
        if (RGB_TO_MAP[rgb] == 0) {
            RGB_TO_MAP[rgb] = findClosestColorMath(rgb).getRenderColor();
        }
        return RGB_TO_MAP[rgb];
    }

    /**
     * Converts canvas to nbt
     */
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

        if (canvas instanceof IconContainer iconContainer) {
            var iconsSource = iconContainer.getIcons();

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
        }

        return nbt;
    }

    public static BufferedImage toImage(DrawableCanvas canvas) {
        var image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();

        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                var color = canvas.get(x, y);

                if (color.getColor() != MapColor.CLEAR) {
                    image.setRGB(x, y, color.getRgbColor() | 0xFF000000);
                }
            }
        }
        return image;
    }

    private static CanvasColor findClosestColorMath(int rgb) {
        int shortestDistance = Integer.MAX_VALUE;
        var out = CanvasColor.CLEAR;

        final int redColor = (rgb >> 16) & 0xFF;
        final int greenColor = (rgb >> 8) & 0xFF;
        final int blueColor = rgb & 0xFF;

        final var array = CanvasColor.values();
        final int length = array.length;

        for (int i = 0; i < length; i++) {
            final var canvasColor = array[i];
            if (canvasColor.getColor() == MapColor.CLEAR) {
                continue;
            }

            final int tmpColor = canvasColor.getRgbColor();

            final int redCanvas = (tmpColor >> 16) & 0xFF;
            final int greenCanvas = (tmpColor >> 8) & 0xFF;
            final int blueCanvas = (tmpColor) & 0xFF;

            final int distance = MathHelper.square(redCanvas - redColor) + MathHelper.square(greenCanvas - greenColor) + MathHelper.square(blueCanvas - blueColor);

            if (distance < shortestDistance) {
                out = canvasColor;
                shortestDistance = distance;
            }
        }

        return out;
    }
}
