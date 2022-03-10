package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
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
        var id = ((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId();
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(id);

        if (display != null) {
            packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
                @Override
                public void interact(Hand hand) {
                }

                @Override
                public void interactAt(Hand hand, Vec3d pos) {
                    display.interactAt(player, id, pos, hand, false);
                }

                @Override
                public void attack() {
                    display.interactAt(player, id, null, Hand.MAIN_HAND, true);
                }
            });
        }
    }
}
