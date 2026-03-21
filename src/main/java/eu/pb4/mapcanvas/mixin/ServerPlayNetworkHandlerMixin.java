package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handlePickItemFromEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;"))
    private void mapcanvas_handlePickBlock(ServerboundPickItemFromEntityPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(packet.id());
        if (display != null) {
            display.interactAt(player, packet.id(), null, InteractionHand.MAIN_HAND, packet.includeData() ? VirtualDisplay.ClickType.MIDDLE_CTRL : VirtualDisplay.ClickType.MIDDLE);
        }
    }

    @Inject(method = "handleInteract", at = @At("TAIL"))
    private void mapcanvas_handleDisplayEntity(ServerboundInteractPacket packet, CallbackInfo ci) {
        var id = ((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId();

        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(id);

        if (display != null) {
            packet.dispatch(new ServerboundInteractPacket.Handler() {
                @Override
                public void onInteraction(InteractionHand hand) {
                }

                @Override
                public void onInteraction(InteractionHand hand, Vec3 pos) {
                    display.interactAt(player, id, pos, hand, VirtualDisplay.ClickType.RIGHT);
                }

                @Override
                public void onAttack() {
                    display.interactAt(player, id, null, InteractionHand.MAIN_HAND, VirtualDisplay.ClickType.LEFT);
                }
            });
        }
    }
}
