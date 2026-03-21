package eu.pb4.mapcanvas.testmod;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ByteFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;


import java.util.Arrays;
import java.util.Map;

public record MinecraftWorldCanvas(Level world, Object2ByteFunction<BlockState> blockStateConverter, Byte2ObjectFunction<BlockState> colorConverter,
                                   Direction directionHorizontal, Direction directionVertical, BlockPos blockPos) implements DrawableCanvas {
    private static final BlockPos.MutableBlockPos MUT = new BlockPos.MutableBlockPos();

    private static BlockState[] states;

    public static MinecraftWorldCanvas of(Level world, BlockPos start, Direction directionVertical, Direction directionHorizontal) {
        states = null;
        if (states == null) {
            states = new BlockState[256];
            Arrays.fill(states, Blocks.AIR.defaultBlockState());
            for (var block : BuiltInRegistries.BLOCK) {
                var color = block.defaultMapColor();
                if (color == MapColor.NONE || !block.defaultBlockState().isCollisionShapeFullBlock(world, BlockPos.ZERO)) {
                    continue;
                }
                for (var b : MapColor.Brightness.values()) {
                    states[Byte.toUnsignedInt(color.getPackedId(b))] = block.defaultBlockState();
                }
            }
        }

        return new MinecraftWorldCanvas(world, x -> ((BlockState) x).getMapColor(null, BlockPos.ZERO).getPackedId(MapColor.Brightness.HIGH), b -> states[Byte.toUnsignedInt(b)], directionHorizontal, directionVertical, start);
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
        world.setBlockAndUpdate(asPos(x, y), colorConverter.get(color));
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
