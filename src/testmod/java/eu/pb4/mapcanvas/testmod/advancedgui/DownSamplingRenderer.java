package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.*;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.testmod.OkLab;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.MapColor;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class DownSamplingRenderer implements ActiveRenderer {
    public BufferedImage source;
    public CanvasImage image1;
    public CanvasImage image2;

    public DownSamplingRenderer() {}

    private byte getColor(BufferedImage bufferedImage, int x, int y, Int2ObjectOpenHashMap<CanvasColor> cache) {
        var rgb = bufferedImage.getRGB(x, y);
        if (cache.containsKey(rgb)) {
            return cache.get(rgb).getRenderColor();
        }

        var shortestDistance = Float.MAX_VALUE;
        var out = CanvasColor.CLEAR;

        final var source = OkLab.fromRgb(rgb);

        final var array = CanvasColor.values();
        final int length = array.length;

        for (int i = 0; i < length; i++) {
            final var canvasColor = array[i];
            if (canvasColor.getColor() == MapColor.CLEAR) {
                continue;
            }

            final int tmpColor = canvasColor.getRgbColor();

            final var target = OkLab.fromRgb(tmpColor);

            final var distance = Math.abs(target.l() - source.l()) + Math.abs(target.a() - source.a()) + Math.abs(target.b() - source.b());

            if (distance < shortestDistance) {
                out = canvasColor;
                shortestDistance = distance;
            }
        }

        cache.put(rgb, out);

        return out.getRenderColor();
    }

    @Override
    public void setup(PlayerCanvas canvas) {
        try {
            this.source = ImageIO.read(new URL("https://i.stack.imgur.com/MlIL8.png"));
            //this.source = ImageIO.read(new URL("https://i.imgur.com/Xa2KyPN.png"));
            this.image1 = CanvasImage.from(this.source, CanvasImage.ColorResolver.DEFAULT);
            this.image2 = null;
        } catch (Throwable e) {

        }
    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        if (this.image1 == null) {
            return;
        }
        CanvasUtils.clear(canvas, CanvasColor.BLACK_LOWEST);
        CanvasUtils.draw(canvas, 0, 0, this.image1);

        DefaultFonts.UNSANDED.drawText(canvas,  this.image2 == null ? "Default" : "Custom", 512 + 16, 16, 32, CanvasColor.WHITE_HIGH);
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
        if (this.image2 == null) {
            this.image2 = this.image1;
            var cache = new Int2ObjectOpenHashMap<CanvasColor>();
            this.image1 = CanvasImage.from(this.source, (buf, xa, ya) -> this.getColor(buf, xa, ya, cache));
        } else {
            this.image1 = this.image2;
            this.image2 = null;
        }
    }
}
