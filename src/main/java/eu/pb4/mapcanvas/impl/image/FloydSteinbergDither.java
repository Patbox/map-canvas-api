package eu.pb4.mapcanvas.impl.image;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class FloydSteinbergDither {
    public static CanvasColor sample(RawImage scratchImg, int x, int y, CanvasUtils.ColorMapper mapper) {
        var imageColor = scratchImg.get(x, y);
        var closestColor = CanvasColor.getFromRaw(mapper.getRawColor(imageColor));
        var palletedColor = closestColor.getRgbColor();

        var errorR = ARGB.red(imageColor) - ARGB.red(palletedColor);
        var errorG = ARGB.green(imageColor) - ARGB.green(palletedColor);
        var errorB = ARGB.blue(imageColor) - ARGB.blue(palletedColor);
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
        int pR = Mth.clamp( ARGB.red(pixelColor) + (int) ((double) errorR * quantConst), 0, 255);
        int pG = Mth.clamp(ARGB.green(pixelColor) + (int) ((double) errorG * quantConst), 0, 255);
        int pB = Mth.clamp(ARGB.blue(pixelColor) + (int) ((double) errorB * quantConst), 0, 255);
        return ARGB.color(ARGB.alpha(pixelColor), pR, pG, pB);
    }
}
