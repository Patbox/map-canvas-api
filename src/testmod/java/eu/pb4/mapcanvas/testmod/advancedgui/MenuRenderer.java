package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.function.Consumer;

public class MenuRenderer implements ActiveRenderer {
    private final Consumer<ActiveRenderer> setRenderer;
    private final List<Pair<String, ActiveRenderer>> renderers;
    private final int scroll;

    public MenuRenderer(Consumer<ActiveRenderer> rendererSetter, List<Pair<String, ActiveRenderer>> renderers) {
        this.setRenderer = rendererSetter;
        this.renderers = renderers;
        this.scroll = 0;
    }

    @Override
    public void setup(PlayerCanvas canvas) { }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        CanvasUtils.clear(canvas, CanvasColor.WHITE_NORMAL);
        var renderHeight = canvas.getHeight() + 15;
        var i = 0;
        for (var pair : this.renderers) {
            int y = 16 + i * 20 - this.scroll;
            if (y > -15 && y < renderHeight) {
                DefaultFonts.VANILLA.drawText(canvas, "» " + pair.getLeft(), 16, y, 16, CanvasColor.BLACK_NORMAL);
            }
            i++;
        }
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
        var index = (y + this.scroll - 16) / 20;

        if (index >= 0 && index < this.renderers.size() ) {
            this.setRenderer.accept(this.renderers.get(index).getRight());
        }
    }
}
