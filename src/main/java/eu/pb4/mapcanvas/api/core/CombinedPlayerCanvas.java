package eu.pb4.mapcanvas.api.core;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface CombinedPlayerCanvas extends CombinedCanvas, PlayerCanvas {
    @Nullable PlayerCanvas getSubCanvas(int x, int y);

    @Override
    default int getId() {
        return 0;
    }

    @Override
    default ItemStack asStack() {
        return new ItemStack(Items.FILLED_MAP);
    }

    int getIdOf(int x, int y);
    ItemStack asStackOf(int x, int y);
}
