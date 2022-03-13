package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onPlayerInteractEntity", at = @At("TAIL"))
    private void mapcanvas_handleDisplay(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId());

        if (display != null) {
            display.handleInteractionPacket(packet, player);
        }
    }
}
