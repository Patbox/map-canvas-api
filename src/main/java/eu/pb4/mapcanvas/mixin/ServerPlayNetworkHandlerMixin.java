package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
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

    @Inject(method = "handleAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;"))
    private void mapcanvas_handleAttack(ServerboundAttackPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(packet.entityId());
        if (display != null) {
            display.interactAt(player, packet.entityId(), null, InteractionHand.MAIN_HAND, VirtualDisplay.ClickType.RIGHT);
        }
    }


    @Inject(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;"))
    private void mapcanvas_handleDisplayEntity(ServerboundInteractPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(packet.entityId());

        if (display != null) {
            display.interactAt(this.player, packet.entityId(), packet.location(), packet.hand(), VirtualDisplay.ClickType.RIGHT);
        }
    }
}
