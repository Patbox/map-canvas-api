package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RaycastRenderer implements ActiveRenderer {
    private final MinecraftServer server;
    private Entity entity = null;

    public RaycastRenderer(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void setup(PlayerCanvas canvas) {

    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        if (this.entity == null || this.entity.isRemoved()) {
            for (var entity : this.server.overworld().getAllEntities()) {
                if (entity.getCustomName() != null && entity.getCustomName().getString().equals("Camera")) {
                    this.entity = entity;
                    return;
                }
            }

            if (this.server.getPlayerList().getPlayers().size() > 0 && false) {
                this.entity = this.server.getPlayerList().getPlayers().getFirst();
                return;
            }

            DefaultFonts.UNSANDED.drawText(canvas, "Couldn't find \"Camera\" entity!", 33, 33, 16, CanvasColor.BLACK_HIGH);
            DefaultFonts.UNSANDED.drawText(canvas, "Couldn't find \"Camera\" entity!", 32, 32, 16, CanvasColor.BRIGHT_RED_HIGH);
            return;
        }

        final int pixelSize = 16;

        int width = canvas.getWidth() / pixelSize;
        int height = canvas.getHeight() / pixelSize;

        var halfWidth = width / 2;
        var halfHeight = height / 2;

        var yaw = this.entity.getYRot();
        var pitch = this.entity.getXRot();

        var pos = new Vec3(this.entity.getX(), this.entity.getY() + this.entity.getEyeHeight(), this.entity.getZ());

        var world =  this.entity.level();
        for (var x = 0; x < width; x++) {
            float yawAngle = -(yaw + (x - halfWidth) / 4f * pixelSize) * Mth.RAD_TO_DEG;
            float yawCos = Mth.cos(yawAngle);
            float yawSin = Mth.sin(yawAngle);

            for (var y = 0; y < height; y++) {

                Vec3 max;
                {
                    float pitchAngle = (pitch + (y - halfHeight) / 4f * pixelSize) * Mth.RAD_TO_DEG;
                    float pitchCos = Mth.cos(pitchAngle);
                    float pitchSin = Mth.sin(pitchAngle);

                    max = pos.add((yawSin * pitchCos) * 16, (-pitchSin) * 16, (yawCos * pitchCos) * 16);
                }
                var context = new ClipContext(pos, max, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, this.entity);

                var cast = BlockGetter.traverseBlocks(pos, max, null, (contextX, blockPos) -> {
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.isAir()) {
                        return null;
                    }
                    FluidState fluidState = world.getFluidState(blockPos);
                    VoxelShape voxelShape = context.getBlockShape(blockState, world, blockPos);
                    BlockHitResult blockHitResult = world.clipWithInteractionOverride(pos, max, blockPos, voxelShape, blockState);
                    VoxelShape voxelShape2 = context.getFluidShape(fluidState, world, blockPos);
                    BlockHitResult blockHitResult2 = voxelShape2.clip(pos, max, blockPos);
                    double d = blockHitResult == null ? 1.7976931348623157E308D : pos.distanceToSqr(blockHitResult.getLocation());
                    double e = blockHitResult2 == null ? 1.7976931348623157E308D : pos.distanceToSqr(blockHitResult2.getLocation());
                    return d <= e ? blockHitResult : blockHitResult2;
                }, (contextX) -> null);

                int pX = x * pixelSize;
                int pY = y * pixelSize;

                if (cast != null) {
                    var state = this.entity.level().getBlockState(cast.getBlockPos());

                    var mapColor = state.getMapColor(this.entity.level(), cast.getBlockPos());
                    if (mapColor == MapColor.NONE) {
                        mapColor = MapColor.COLOR_MAGENTA;
                    }

                    boolean border = !switch (cast.getDirection()) {
                        case UP, DOWN -> Math.abs(cast.getBlockPos().getX() + 0.5 - cast.getLocation().x) > 0.4 || Math.abs(cast.getBlockPos().getZ() + 0.5 - cast.getLocation().z) > 0.4;
                        case EAST, WEST -> Math.abs(cast.getBlockPos().getZ() + 0.5 - cast.getLocation().z) > 0.4 || Math.abs(cast.getBlockPos().getY() + 0.5 - cast.getLocation().y) > 0.4;
                        case NORTH, SOUTH -> Math.abs(cast.getBlockPos().getX() + 0.5 - cast.getLocation().x) > 0.4 || Math.abs(cast.getBlockPos().getY() + 0.5 - cast.getLocation().y) > 0.4;
                    };

                    var brightness = switch (cast.getDirection().getAxis()) {
                        case Z -> border ? MapColor.Brightness.NORMAL : MapColor.Brightness.LOW;
                        case X -> border ? MapColor.Brightness.LOW : MapColor.Brightness.LOWEST;
                        case Y -> cast.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE
                                ? border ? MapColor.Brightness.HIGH : MapColor.Brightness.NORMAL
                                : border ? MapColor.Brightness.LOW : MapColor.Brightness.LOWEST;
                    };


                    var color = CanvasColor.from(mapColor, brightness);

                    for (int sPX = 0; sPX < pixelSize; sPX++) {
                        for (int sPY = 0; sPY < pixelSize; sPY++) {
                            outputCanvas.set(pX + sPX, pY + sPY, color);
                        }
                    }
                } else {
                    for (int sPX = 0; sPX < pixelSize; sPX++) {
                        for (int sPY = 0; sPY < pixelSize; sPY++) {
                            outputCanvas.set(pX + sPX, pY + sPY, CanvasColor.LAPIS_BLUE_HIGH);
                        }
                    }
                }

                for (int sPX = 0; sPX < pixelSize; sPX++) {
                    for (int sPY = 0; sPY < pixelSize; sPY++) {
                        outputCanvas.set(pX + sPX, pY + sPY + pixelSize, CanvasColor.BRIGHT_RED_HIGH);
                    }
                }
                CanvasUtils.fill(outputCanvas, 14, outputCanvas.getHeight() - 38, 80, outputCanvas.getHeight(), CanvasColor.CLEAR);
                DefaultFonts.UNSANDED.drawText(outputCanvas, "X: " + x, 16, outputCanvas.getHeight() - 34, 16, CanvasColor.WHITE_NORMAL);
                DefaultFonts.UNSANDED.drawText(outputCanvas, "Y: " + y, 16, outputCanvas.getHeight() - 18, 16, CanvasColor.WHITE_NORMAL);
                outputCanvas.sendUpdates();
            }
        }
    }

    @Override
    public void onClick(ServerPlayer player, VirtualDisplay.ClickType type, int x, int y) {

    }
}
