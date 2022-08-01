package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.impl.PlayerInterface;
import net.minecraft.client.particle.Particle;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

    /*@Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    private void mapcanvas_handleDisplayRaycast(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if (this.mapcanvas_checkRaycast()) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    private void mapcanvas_handleDisplayRaycast2(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        if (this.mapcanvas_checkRaycast()) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    private void mapcanvas_handleDisplayRaycast3(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        if (this.mapcanvas_checkRaycast()) {
            ci.cancel();
        }
    }*/

    private boolean mapcanvas_checkRaycast() {
        var set = ((PlayerInterface) this.player).mapcanvas_getBoxes();

        if (!set.isEmpty()) {
            var d = 9;
            var cameraVec = this.player.getCameraPosVec(0);
            var rotationVec = this.player.getRotationVec(0);
            var endVec = cameraVec.add(rotationVec.x * d, rotationVec.y * d, rotationVec.z * d);

            for (var entry : set) {
                System.out.println(entry.getValue());
                var opt = entry.getValue().raycast(cameraVec, endVec);

                if (opt.isPresent()) {
                    System.out.println(opt.get());
                    this.player.networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.NOTE, true, opt.get().x, opt.get().y, opt.get().z, 0, 0, 0, 0, 0));

                    this.player.networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.DOLPHIN, true, entry.getValue().minX, entry.getValue().minY, entry.getValue().minZ, 0, 0, 0, 0, 0));
                    this.player.networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.DOLPHIN, true, entry.getValue().maxX, entry.getValue().maxY, entry.getValue().maxZ, 0, 0, 0, 0, 0));

                    return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "onPlayerInteractEntity", at = @At("TAIL"))
    private void mapcanvas_handleDisplayEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        var display = ((PlayerInterface) this.player).mapcanvas_getDisplay(((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId());

        if (display != null) {
            display.handleInteractionPacket(packet, player);
        }
    }
}
