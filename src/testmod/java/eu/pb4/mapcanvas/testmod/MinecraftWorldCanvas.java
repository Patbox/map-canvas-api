package eu.pb4.mapcanvas.testmod;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Map;

public record MinecraftWorldCanvas(World world, Object2ByteFunction<BlockState> blockStateConverter, Byte2ObjectFunction<BlockState> colorConverter,
                                   Direction directionHorizontal, Direction directionVertical, BlockPos blockPos) implements DrawableCanvas {
    private static final BlockPos.Mutable MUT = new BlockPos.Mutable();

    private static BlockState[] states;

    public static MinecraftWorldCanvas of(World world, BlockPos start, Direction directionVertical, Direction directionHorizontal) {
        states = null;
        if (states == null) {
            states = new BlockState[256];
            Arrays.fill(states, Blocks.AIR.getDefaultState());
            for (var block : Registries.BLOCK) {
                var color = block.getDefaultMapColor();
                if (color == MapColor.CLEAR || !block.getDefaultState().isFullCube(world, BlockPos.ORIGIN)) {
                    continue;
                }
                for (var b : MapColor.Brightness.values()) {
                    states[Byte.toUnsignedInt(color.getRenderColorByte(b))] = block.getDefaultState();
                }
            }
        }

        return new MinecraftWorldCanvas(world, x -> ((BlockState) x).getMapColor(null, BlockPos.ORIGIN).getRenderColorByte(MapColor.Brightness.HIGH), b -> states[Byte.toUnsignedInt(b)], directionHorizontal, directionVertical, start);
    }

    @Override
    public byte getRaw(int x, int y) {
        return blockStateConverter.getByte(world.getBlockState(asPos(x, y)));
    }

    private BlockPos asPos(int x, int y) {
        return MUT.set(blockPos).move(directionHorizontal, x).move(directionVertical, y);
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        world.setBlockState(asPos(x, y), colorConverter.get(color), Block.SKIP_DROPS | Block.FORCE_STATE | Block.NOTIFY_LISTENERS);
    }

    @Override
    public int getHeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getWidth() {
        return Integer.MAX_VALUE;
    }
}
