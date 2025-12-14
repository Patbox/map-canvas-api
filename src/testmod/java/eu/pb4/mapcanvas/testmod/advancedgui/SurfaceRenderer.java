package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.ViewUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;


public class SurfaceRenderer implements ActiveRenderer {
    private final SimplexNoiseSampler noise;

    public SurfaceRenderer() {
        this.noise = new SimplexNoiseSampler(new Xoroshiro128PlusPlusRandom(1));
    }
    
    @Override
    public void setup(PlayerCanvas canvas) { }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        final var sin = Math.sin(time / 1000d) * 2;
        final var colorSize = (61 * 4 - 4);
        final var width = canvas.getWidth() / 2;
        final var height = canvas.getHeight() / 2;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var color = CanvasColor.values()[(int) ((
                                        (
                                                (this.noise.sample((x / 80d + sin *2) + Math.sin(y / 40d), y / 80d + (time / 1000d) % 3600 + Math.sin(x / 40d) * 2, (time / 600d) % 3600)
                                        ) * 8 + time / 400) % colorSize) + 4)];


                canvas.set(x * 2, y * 2, color);
                canvas.set(x * 2, y * 2 + 1, color);
                canvas.set(x * 2 + 1, y * 2 + 1, color);
                canvas.set(x * 2 + 1, y * 2, color);
            }
        }

        var text = """
                Is this a boss battle?
                Idk
                """;


        var textView = ViewUtils.transform(canvas, new ViewUtils.Transformer() {
            @Override
            public Point transform(int x, int y) {
                return new ViewUtils.Transformer.Point((int) Math.floor(x + Math.sin(y / 10d + time / 500d) * 2), (int) Math.floor(y + Math.sin(x / 10d + time / 500d) * 2));
            }
        });

        var font = DefaultFonts.REGISTRY.getFontOrElse(Identifier.of("roboto", "bold/aa"), DefaultFonts.UNSANDED);

        font.drawText(textView, text, 17, 17, 32, CanvasColor.GRAY_HIGH);
        font.drawText(textView, text, 16, 16, 32, CanvasColor.WHITE_HIGH);
    }

    @Override
    public void onClick(ServerPlayerEntity player, VirtualDisplay.ClickType type, int x, int y) {

    }
}
