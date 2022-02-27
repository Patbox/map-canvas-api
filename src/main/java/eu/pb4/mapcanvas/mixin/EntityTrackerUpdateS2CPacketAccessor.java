package eu.pb4.mapcanvas.mixin;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(EntityTrackerUpdateS2CPacket.class)
public interface EntityTrackerUpdateS2CPacketAccessor {
    @Mutable
    @Accessor("id")
    void setId(int id);

    @Mutable
    @Accessor("trackedValues")
    void setTrackedValues(List<DataTracker.Entry<?>> data);
}
