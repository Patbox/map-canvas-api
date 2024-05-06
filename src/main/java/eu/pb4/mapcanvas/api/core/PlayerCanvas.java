package eu.pb4.mapcanvas.api.core;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface PlayerCanvas extends DrawableCanvas, IconContainer {
    default boolean addPlayer(ServerPlayerEntity player) {
        return this.addPlayer(player.networkHandler);
    }

    default boolean removePlayer(ServerPlayerEntity player) {
        return this.removePlayer(player.networkHandler);
    }

    boolean addPlayer(ServerPlayNetworkHandler player);

    boolean removePlayer(ServerPlayNetworkHandler player);

    void sendUpdates();

    boolean isDirty();

    int getId();
    MapIdComponent getIdComponent();

    default int getIconHeight() {
        return this.getHeight() * 2;
    }

    default int getIconWidth() {
        return this.getWidth() * 2;
    }

    default ItemStack asStack() {
        var stack = new ItemStack(Items.FILLED_MAP);
        stack.set(DataComponentTypes.MAP_ID, this.getIdComponent());
        return stack;
    }

    void destroy();
    boolean isDestroyed();
}
