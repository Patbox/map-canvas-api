package eu.pb4.mapcanvas.api.core;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public interface IconContainer {
    default CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, int x, int y) {
        return this.createIcon(type, x, y, (byte) 0, null);
    }

    default CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, int x, int y, @Nullable Text text) {
        return this.createIcon(type, x, y, (byte) 0, text);
    }

    default CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, int x, int y, byte rotation, @Nullable Text text) {
        return this.createIcon(type, true, x, y,  (byte) 0, text);
    }

    Collection<CanvasIcon> getIcons();

    CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, boolean visible, int x, int y, byte rotation, @Nullable Text text);

    CanvasIcon createIcon();

    void removeIcon(CanvasIcon icon);

    default void clearIcons() {
        for (var icon : new ArrayList<>(this.getIcons())) {
            icon.remove();
        }
    }
}