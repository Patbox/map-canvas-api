package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;

public class ClientRenderer implements ActiveRenderer {
    private NativeImage source = null;

    @Override
    public void setup(PlayerCanvas canvas) {

    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        MinecraftClient.getInstance().execute(() -> {
           ScreenshotRecorder.takeScreenshot(MinecraftClient.getInstance().getFramebuffer(), nativeImage -> {
               var oldImage = this.source;
               this.source = nativeImage;
               if (oldImage != null) {
                    oldImage.close();
                }
            });
        });

        if (this.source != null) {
            var imageWidth = this.source.getWidth();
            var imageHeight = this.source.getHeight();

            var canvasWidth = canvas.getWidth();
            var canvasHeight = canvas.getHeight();

            double scale = (double) imageHeight / canvasHeight;

            int xOffset = (int) ((imageWidth - canvasWidth * scale) / 2);

            for (int x = 0; x < canvas.getWidth(); x++) {
                for (int y = 0; y < canvas.getHeight(); y++) {
                    int iX = (int) (x * scale + xOffset);
                    int iY = (int) (y * scale);

                    if (iX >= 0 && iX < imageWidth && iY < imageHeight) {
                        int color = this.source.getColorArgb(iX, iY);

                        final int redCanvas = (color) & 0xFF;
                        final int greenCanvas = (color >> 8) & 0xFF;
                        final int blueCanvas = (color >> 16) & 0xFF;

                        canvas.setRaw(x, y, CanvasUtils.findClosestRawColor(redCanvas << 16 | greenCanvas << 8 | blueCanvas));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {

    }
}
