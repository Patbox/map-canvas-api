package eu.pb4.mapcanvas.api.core;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface CanvasIcon {
    RegistryEntry<MapDecorationType> getType();

    void setType(RegistryEntry<MapDecorationType> type);

    int getX();

    int getY();

    byte getRotation();

    void move(int x, int y, byte rotation);

    boolean isVisible();

    void setVisibility(boolean visibility);

    @Nullable
    Text getText();

    void setText(Text text);

    <T extends DrawableCanvas & IconContainer> T getOwningCanvas();

    default void remove() {
        this.getOwningCanvas().removeIcon(this);
    }
}
