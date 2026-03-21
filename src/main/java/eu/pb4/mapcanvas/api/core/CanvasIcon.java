package eu.pb4.mapcanvas.api.core;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface CanvasIcon {
    Holder<MapDecorationType> getType();

    void setType(Holder<MapDecorationType> type);

    int getX();

    int getY();

    byte getRotation();

    void move(int x, int y, byte rotation);

    boolean isVisible();

    void setVisibility(boolean visibility);

    @Nullable
    Component getText();

    void setText(Component text);

    <T extends DrawableCanvas & IconContainer> T getOwningCanvas();

    default void remove() {
        this.getOwningCanvas().removeIcon(this);
    }
}
