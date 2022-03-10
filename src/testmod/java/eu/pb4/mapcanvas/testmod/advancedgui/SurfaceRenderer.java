package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;


public class SurfaceRenderer implements ActiveRenderer {
    private final DoublePerlinNoiseSampler noise;

    public SurfaceRenderer() {
        this.noise = DoublePerlinNoiseSampler.create(new Xoroshiro128PlusPlusRandom(1), 1, 1.4, 1.0, -0.5, -0.1);
    }
    
    @Override
    public void setup(PlayerCanvas canvas) { }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        var sin = Math.sin(time / 1000d);
        var cos = Math.cos(time / 1000d);
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getWidth(); y++) {
                canvas.set(x, y, switch (Math.max(Math.min((int) ((this.noise.sample((x / 80d + sin), y / 80d + (time / 2000d) % 3600, 0)) * 6), 6), -3)) {
                    case 6 -> CanvasColor.WHITE_NORMAL;
                    case 5 -> CanvasColor.GRAY_HIGH;
                    case 4 -> CanvasColor.LIME_LOW;
                    case 3 -> CanvasColor.LIME_NORMAL;
                    case 2 -> CanvasColor.LIME_HIGH;
                    case 1 -> CanvasColor.PALE_YELLOW_HIGH;
                    case 0 -> CanvasColor.BLUE_HIGH;
                    case -1 -> CanvasColor.BLUE_NORMAL;
                    case -2 -> CanvasColor.BLUE_LOW;
                    case -3 -> CanvasColor.BLUE_LOWEST;

                    default -> CanvasColor.BLACK_NORMAL;
                });
            }
        }
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {

    }
}
