package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;

public interface ActiveRenderer {
    void setup(PlayerCanvas canvas);
    void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame);
    void onClick(ServerPlayerEntity player, ClickType type, int x, int y);
}
