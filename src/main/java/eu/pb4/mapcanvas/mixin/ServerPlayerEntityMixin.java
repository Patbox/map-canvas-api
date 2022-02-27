package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements PlayerInterface {
    private final Int2ObjectMap<VirtualDisplay> mapcanvas_displays = new Int2ObjectOpenHashMap<>();

    @Override
    public void mapcanvas_addDisplay(IntList ids, VirtualDisplay display) {
        for (int id : ids) {
            this.mapcanvas_displays.put(id, display);
        }
    }

    @Override
    public void mapcanvas_removeDisplay(IntList ids) {
        for (int id : ids) {
            this.mapcanvas_displays.remove(id);
        }
    }

    @Override
    public VirtualDisplay mapcanvas_getDisplay(int id) {
        return this.mapcanvas_displays.get(id);
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void mapcanvas_disconnect(CallbackInfo ci) {
        this.mapcanvas_removeAll();
    }

    @Unique
    private void mapcanvas_removeAll() {
        for (var entry : new ArrayList<>(this.mapcanvas_displays.values())) {
            entry.removePlayer((ServerPlayerEntity) (Object) this);
        }
    }
}
