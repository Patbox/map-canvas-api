package eu.pb4.mapcanvas.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFrame.class)
public interface ItemFrameEntityAccessor {
    @Accessor("DATA_ITEM")
    static EntityDataAccessor<ItemStack> getItemStack() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_ROTATION")
    static EntityDataAccessor<Integer> getRotation() {
        throw new UnsupportedOperationException();
    }
}
