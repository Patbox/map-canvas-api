package eu.pb4.mapcanvas.impl.image;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class FloydSteinbergDither {
    public static CanvasColor sample(RawImage scratchImg, int x, int y, CanvasUtils.ColorMapper mapper) {
        var imageColor = scratchImg.get(x, y);
        var closestColor = CanvasColor.getFromRaw(mapper.getRawColor(imageColor));
        var palletedColor = closestColor.getRgbColor();

        var errorR = ColorHelper.getRed(imageColor) - ColorHelper.getRed(palletedColor);
        var errorG = ColorHelper.getGreen(imageColor) - ColorHelper.getGreen(palletedColor);
        var errorB = ColorHelper.getBlue(imageColor) - ColorHelper.getBlue(palletedColor);
        if (scratchImg.width() > x + 1) {
            scratchImg.set(x + 1, y, applyError(scratchImg.get(x + 1, y), errorR, errorG, errorB, 7.0 / 16.0));
        }
        if (scratchImg.height() > y + 1) {
            if (x > 0) {
                scratchImg.set(x - 1, y + 1, applyError(scratchImg.get(x - 1, y + 1), errorR, errorG, errorB, 3.0 / 16.0));
            }
            scratchImg.set(x, y + 1, applyError(scratchImg.get(x, y + 1), errorR, errorG, errorB, 5.0 / 16.0));
            if (scratchImg.width() > x + 1) {
                scratchImg.set(x + 1, y + 1, applyError(scratchImg.get(x + 1, y + 1), errorR, errorG, errorB, 1.0 / 16.0));
            }
        }

        return closestColor;
    }

    private static int applyError(int pixelColor, int errorR, int errorG, int errorB, double quantConst) {
        int pR = MathHelper.clamp( ColorHelper.getRed(pixelColor) + (int) ((double) errorR * quantConst), 0, 255);
        int pG = MathHelper.clamp(ColorHelper.getGreen(pixelColor) + (int) ((double) errorG * quantConst), 0, 255);
        int pB = MathHelper.clamp(ColorHelper.getBlue(pixelColor) + (int) ((double) errorB * quantConst), 0, 255);
        return ColorHelper.getArgb(ColorHelper.getAlpha(pixelColor), pR, pG, pB);
    }
}
