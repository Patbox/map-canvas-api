package eu.pb4.mapcanvas.api.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface PlayerCanvas extends DrawableCanvas, IconContainer {
    default boolean addPlayer(ServerPlayer player) {
        return this.addPlayer(player.connection);
    }

    default boolean removePlayer(ServerPlayer player) {
        return this.removePlayer(player.connection);
    }

    boolean addPlayer(ServerGamePacketListenerImpl player);

    boolean removePlayer(ServerGamePacketListenerImpl player);

    void sendUpdates();

    boolean isDirty();

    int getId();
    MapId getIdComponent();

    default int getIconHeight() {
        return this.getHeight() * 2;
    }

    default int getIconWidth() {
        return this.getWidth() * 2;
    }

    default ItemStack asStack() {
        var stack = new ItemStack(Items.FILLED_MAP);
        stack.set(DataComponents.MAP_ID, this.getIdComponent());
        return stack;
    }

    void destroy();
    boolean isDestroyed();
}
