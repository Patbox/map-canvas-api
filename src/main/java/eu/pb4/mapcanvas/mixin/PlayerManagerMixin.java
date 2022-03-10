package eu.pb4.mapcanvas.mixin;

import eu.pb4.mapcanvas.impl.MapCanvasImpl;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendScoreboard(Lnet/minecraft/scoreboard/ServerScoreboard;Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void sendFakeTeam(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        player.networkHandler.sendPacket(TeamS2CPacket.updateTeam(MapCanvasImpl.FAKE_TEAM, true));
    }
}
