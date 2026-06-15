package eu.pb4.mapcanvas.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("DATA_SHARED_FLAGS_ID")
    static EntityDataAccessor<Byte> getFlags() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_NO_GRAVITY")
    static EntityDataAccessor<Boolean> getNoGravity() {
        throw new UnsupportedOperationException();
    }
}
