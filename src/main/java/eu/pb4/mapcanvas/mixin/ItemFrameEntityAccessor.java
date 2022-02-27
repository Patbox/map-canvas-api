package eu.pb4.mapcanvas.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemFrameEntity.class)
public interface ItemFrameEntityAccessor {
    @Accessor("ITEM_STACK")
    static TrackedData<ItemStack> getItemStack() {
        throw new UnsupportedOperationException();
    }

    @Accessor("ROTATION")
    static TrackedData<Integer> getRotation() {
        throw new UnsupportedOperationException();
    }
}
