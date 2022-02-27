package eu.pb4.mapcanvas.api.core;

import net.minecraft.item.map.MapIcon;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface CanvasIcon {
    MapIcon.Type getType();

    void setType(MapIcon.Type type);

    int getX();

    int getY();

    byte getRotation();

    void move(int x, int y, byte rotation);

    boolean isVisible();

    void setVisibility(boolean visibility);

    @Nullable
    Text getText();

    void setText(Text text);

    DrawableCanvas getOwningCanvas();

    default void remove() {
        this.getOwningCanvas().removeIcon(this);
    }
}
