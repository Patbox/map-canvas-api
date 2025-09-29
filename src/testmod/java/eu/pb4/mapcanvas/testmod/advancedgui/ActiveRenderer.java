package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ActiveRenderer {
    void setup(PlayerCanvas canvas);
    void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame);
    void onClick(ServerPlayerEntity player, VirtualDisplay.ClickType type, int x, int y);

    default void onInput(ServerPlayerEntity player, String input) {};
    default void setStatus(boolean active) {};
}
