package eu.pb4.mapcanvas.api.core;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;

public interface IconContainer {
    default CanvasIcon createIcon(Holder<MapDecorationType> type, int x, int y) {
        return this.createIcon(type, x, y, (byte) 0, null);
    }

    default CanvasIcon createIcon(Holder<MapDecorationType> type, int x, int y, @Nullable Component text) {
        return this.createIcon(type, x, y, (byte) 0, text);
    }

    default CanvasIcon createIcon(Holder<MapDecorationType> type, int x, int y, byte rotation, @Nullable Component text) {
        return this.createIcon(type, true, x, y, rotation, text);
    }

    Collection<CanvasIcon> getIcons();

    CanvasIcon createIcon(Holder<MapDecorationType> type, boolean visible, int x, int y, byte rotation, @Nullable Component text);

    CanvasIcon createIcon();

    void removeIcon(CanvasIcon icon);

    default void clearIcons() {
        for (var icon : new ArrayList<>(this.getIcons())) {
            icon.remove();
        }
    }
}