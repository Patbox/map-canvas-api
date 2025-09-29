package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.packet.c2s.play.PickItemFromEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
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

    @Inject(method = "onPickItemFromEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getEntityOrDragonPart(I)Lnet/minecraft/entity/Entity;"))
    private void mapcanvas_handlePickBlock(PickItemFromEntityC2SPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(packet.id());
        if (display != null) {
            display.interactAt(player, packet.id(), null, Hand.MAIN_HAND, packet.includeData() ? VirtualDisplay.ClickType.MIDDLE_CTRL : VirtualDisplay.ClickType.MIDDLE);
        }
    }

    @Inject(method = "onPlayerInteractEntity", at = @At("TAIL"))
    private void mapcanvas_handleDisplayEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        var id = ((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId();

        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(id);

        if (display != null) {
            packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
                @Override
                public void interact(Hand hand) {
                }

                @Override
                public void interactAt(Hand hand, Vec3d pos) {
                    display.interactAt(player, id, pos, hand, VirtualDisplay.ClickType.RIGHT);
                }

                @Override
                public void attack() {
                    display.interactAt(player, id, null, Hand.MAIN_HAND, VirtualDisplay.ClickType.LEFT);
                }
            });
        }
    }
}
