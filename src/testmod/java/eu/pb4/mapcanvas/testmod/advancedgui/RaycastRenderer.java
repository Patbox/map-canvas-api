package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

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
            for (var entity : this.server.getOverworld().iterateEntities()) {
                if (entity.getCustomName() != null && entity.getCustomName().asString().equals("Camera")) {
                    this.entity = entity;
                    return;
                }
            }

            if (this.server.getPlayerManager().getPlayerList().size() > 0 && false) {
                this.entity = this.server.getPlayerManager().getPlayerList().get(0);
                return;
            }

            DefaultFonts.UNSANDED.drawText(canvas, "Couldn't find \"Camera\" entity!", 33, 33, 16, CanvasColor.BLACK_HIGH);
            DefaultFonts.UNSANDED.drawText(canvas, "Couldn't find \"Camera\" entity!", 32, 32, 16, CanvasColor.BRIGHT_RED_HIGH);
            return;
        }

        final int pixelSize = 2;

        int width = canvas.getWidth() / pixelSize;
        int height = canvas.getHeight() / pixelSize;

        var halfWidth = width / 2;
        var halfHeight = height / 2;

        var yaw = this.entity.getYaw();
        var pitch = this.entity.getPitch();

        var pos = new Vec3d(this.entity.getX(), this.entity.getY() + this.entity.getStandingEyeHeight(), this.entity.getZ());

        var world =  this.entity.world;
        for (var x = 0; x < width; x++) {
            float yawAngle = (float) -Math.toRadians(yaw + (x - halfWidth) / 4 * pixelSize);
            float yawCos = MathHelper.cos(yawAngle);
            float yawSin = MathHelper.sin(yawAngle);

            for (var y = 0; y < height; y++) {

                Vec3d max;
                {
                    float pitchAngle = (pitch + (y - halfHeight) / 4 * pixelSize) * 0.017453292F;
                    float pitchCos = MathHelper.cos(pitchAngle);
                    float pitchSin = MathHelper.sin(pitchAngle);

                    max = pos.add((yawSin * pitchCos) * 16, (-pitchSin) * 16, (yawCos * pitchCos) * 16);
                }
                var context = new RaycastContext(pos, max, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, this.entity);

                var cast = BlockView.raycast(pos, max, null, (contextX, blockPos) -> {
                    BlockState blockState = world.getBlockState(blockPos);
                    if (blockState.isAir()) {
                        return null;
                    }
                    FluidState fluidState = world.getFluidState(blockPos);
                    VoxelShape voxelShape = context.getBlockShape(blockState, world, blockPos);
                    BlockHitResult blockHitResult = world.raycastBlock(pos, max, blockPos, voxelShape, blockState);
                    VoxelShape voxelShape2 = context.getFluidShape(fluidState, world, blockPos);
                    BlockHitResult blockHitResult2 = voxelShape2.raycast(pos, max, blockPos);
                    double d = blockHitResult == null ? 1.7976931348623157E308D : pos.squaredDistanceTo(blockHitResult.getPos());
                    double e = blockHitResult2 == null ? 1.7976931348623157E308D : pos.squaredDistanceTo(blockHitResult2.getPos());
                    return d <= e ? blockHitResult : blockHitResult2;
                }, (contextX) -> null);

                int pX = x * pixelSize;
                int pY = y * pixelSize;

                if (cast != null) {
                    var state = this.entity.world.getBlockState(cast.getBlockPos());

                    if (state.getBlock() instanceof PillarBlock) {
                        state = switch (state.get(PillarBlock.AXIS)) {
                            case Y -> switch (cast.getSide().getAxis()) {
                                case Y -> state.with(PillarBlock.AXIS, Direction.Axis.Y);
                                case X -> state.with(PillarBlock.AXIS, Direction.Axis.X);
                                case Z -> state.with(PillarBlock.AXIS, Direction.Axis.Z);
                            };
                            case X -> switch (cast.getSide().getAxis()) {
                                case Y -> state.with(PillarBlock.AXIS, Direction.Axis.X);
                                case X -> state.with(PillarBlock.AXIS, Direction.Axis.Y);
                                case Z -> state.with(PillarBlock.AXIS, Direction.Axis.Z);
                            };
                            case Z -> switch (cast.getSide().getAxis()) {
                                case Y -> state.with(PillarBlock.AXIS, Direction.Axis.Z);
                                case X -> state.with(PillarBlock.AXIS, Direction.Axis.X);
                                case Z -> state.with(PillarBlock.AXIS, Direction.Axis.Y);
                            };
                        };
                    }

                    var mapColor = state.getMapColor(this.entity.world, cast.getBlockPos());
                    if (mapColor == MapColor.CLEAR) {
                        mapColor = MapColor.WHITE_GRAY;
                    }

                    boolean border = !switch (cast.getSide()) {
                        case UP, DOWN -> Math.abs(cast.getBlockPos().getX() + 0.5 - cast.getPos().x) > 0.4 || Math.abs(cast.getBlockPos().getZ() + 0.5 - cast.getPos().z) > 0.4;
                        case EAST, WEST -> Math.abs(cast.getBlockPos().getZ() + 0.5 - cast.getPos().z) > 0.4 || Math.abs(cast.getBlockPos().getY() + 0.5 - cast.getPos().y) > 0.4;
                        case NORTH, SOUTH -> Math.abs(cast.getBlockPos().getX() + 0.5 - cast.getPos().x) > 0.4 || Math.abs(cast.getBlockPos().getY() + 0.5 - cast.getPos().y) > 0.4;
                    };

                    var brightness = switch (cast.getSide().getAxis()) {
                        case Z -> border ? MapColor.Brightness.NORMAL : MapColor.Brightness.LOW;
                        case X -> border ? MapColor.Brightness.LOW : MapColor.Brightness.LOWEST;
                        case Y -> cast.getSide().getDirection() == Direction.AxisDirection.POSITIVE
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
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {

    }
}
