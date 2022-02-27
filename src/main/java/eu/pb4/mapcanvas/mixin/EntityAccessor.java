package eu.pb4.mapcanvas.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("CURRENT_ID")
    static AtomicInteger getCurrentId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("FLAGS")
    static TrackedData<Byte> getFlags() {
        throw new UnsupportedOperationException();
    }

    @Accessor("NO_GRAVITY")
    static TrackedData<Boolean> getNoGravity() {
        throw new UnsupportedOperationException();
    }
}
