package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import eu.pb4.mapcanvas.mixin.EntityAccessor;
import eu.pb4.mapcanvas.mixin.EntityTrackerUpdateS2CPacketAccessor;
import eu.pb4.mapcanvas.mixin.ItemFrameEntityAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public sealed abstract class VirtualDisplay permits VirtualDisplay.Combined, VirtualDisplay.Single {
    private final IntList ids = new IntArrayList();
    private final List<Holder> holders = new ArrayList();
    private final Int2ObjectMap<Holder> holderById = new Int2ObjectOpenHashMap<>();
    private final boolean glowing;
    private final BlockPos pos;
    private final Direction direction;
    private final int rotation;
    private final InteractionCallback interactionCallback;
    private final Set<ServerPlayerEntity> players = new HashSet<>();
    private IntList clickableIds = new IntArrayList();

    protected VirtualDisplay(BlockPos pos, boolean glowing, Direction direction, int rotation, InteractionCallback callback) {
        this.glowing = glowing;
        this.pos = pos;
        this.direction = direction;
        this.rotation = rotation;
        this.interactionCallback = callback;
    }

    public final void addPlayer(ServerPlayerEntity player) {
        if (!player.isDisconnected()) {
            for (var holder : this.holders) {
                player.networkHandler.sendPacket(holder.packet1);
                player.networkHandler.sendPacket(holder.packet2);
                if (holder.entityId2 != -1) {
                    player.networkHandler.sendPacket(holder.packet3);
                    player.networkHandler.sendPacket(holder.packet4);
                }
            }
            this.players.add(player);
            ((PlayerInterface) player).mapcanvas_addDisplay(this.clickableIds, this);
        }
    }

    public final void removePlayer(ServerPlayerEntity player) {
        if (!player.isDisconnected()) {
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.ids));
        }
        this.players.remove(player);
        ((PlayerInterface) player).mapcanvas_removeDisplay(this.clickableIds);
    }

    public final void destroy() {
        for (var player : new ArrayList<>(this.players)) {
            this.removePlayer(player);
        }

        for (int id : this.ids) {
            MapIdManager.freeEntityId(id);
        }
        this.holders.clear();
        this.ids.clear();
    }

    protected final void addHolder(PlayerCanvas canvas, int xOffset, int yOffset) {
        var holder = Holder.of(canvas, xOffset, yOffset, this.pos, this.direction, this.rotation, this.glowing, this.interactionCallback != null);
        this.holders.add(holder);
        this.ids.add(holder.entityId);
        this.holderById.put(holder.entityId, holder);

        if (holder.entityId2 != -1) {
            this.ids.add(holder.entityId2);
            this.clickableIds.add(holder.entityId2);
            this.holderById.put(holder.entityId2, holder);
        }
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract PlayerCanvas getCanvas();

    public static final VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing) {
        return of(canvas, pos, direction, rotation, glowing, null);
    }

    public static final VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing, @Nullable InteractionCallback callback) {
        if (canvas instanceof CombinedPlayerCanvas combinedCanvas) {
            return new Combined(combinedCanvas, pos, glowing, direction, Math.abs(rotation % 4), callback);
        } else {
            return new Single(canvas, pos, glowing, direction, Math.abs(rotation % 4), callback);
        }
    }

    @ApiStatus.Internal
    public final void interactAt(ServerPlayerEntity player, int id, Vec3d pos, Hand hand) {
        if (this.interactionCallback != null) {
            var holder = this.holderById.get(id);

            double sourceX, sourceY, tmp;

            if (this.direction.getAxis() == Direction.Axis.X) {
                sourceX = 0.5 - pos.z * this.direction.getOffsetX();
                sourceY = 1 - pos.y;
            } if (this.direction.getAxis() == Direction.Axis.Z) {
                sourceX = 0.5 - pos.x * -this.direction.getOffsetZ();
                sourceY = 1 - pos.y;
            } else {
                sourceX = 0.5 + pos.x;
                sourceY = 0.5 + pos.z;
            }

            switch (rotation) {
                case 1 -> {
                    tmp = 1 - sourceX;
                    sourceX = sourceY;
                    sourceY = tmp;
                }
                case 2 -> {
                    sourceX = 1 - sourceX;
                    sourceY = 1 - sourceY;
                }
                case 3 -> {
                    tmp = sourceX;
                    sourceX = 1 - sourceY;
                    sourceY = tmp;
                }
            }

            int x = (int) ((sourceX + holder.xOffset)  * CanvasUtils.MAP_DATA_SIZE);
            int y = (int) ((sourceY + holder.yOffset) * CanvasUtils.MAP_DATA_SIZE);
            this.interactionCallback.onClick(player, x, y);

        }
    }

    public interface InteractionCallback {
        void onClick(ServerPlayerEntity player, int x, int y);
    }

    protected static final class Single extends VirtualDisplay {
        private final PlayerCanvas canvas;

        private Single(PlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, InteractionCallback callback) {
            super(pos, glowing, direction, rotation, callback);
            this.canvas = canvas;
            this.addHolder(this.canvas, 0, 0);
        }

        @Override
        public int getHeight() {
            return 1;
        }

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public PlayerCanvas getCanvas() {
            return this.canvas;
        }
    }

    protected static final class Combined extends VirtualDisplay {
        private final int width;
        private final int height;
        private final CombinedPlayerCanvas canvas;

        private Combined(CombinedPlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, InteractionCallback callback) {
            super(pos, glowing, direction, rotation, callback);
            this.width = canvas.getSectionsWidth();
            this.height = canvas.getSectionsHeight();
            this.canvas = canvas;


            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    this.addHolder(this.canvas.getSubCanvas(x, y), x, y);
                }
            }

        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public PlayerCanvas getCanvas() {
            return this.canvas;
        }
    }

    protected record Holder(int entityId, int entityId2, int xOffset, int yOffset, UUID uuid, Packet<?> packet1, Packet<?> packet2, Packet<?> packet3, Packet<?> packet4) {

        public static Holder of(PlayerCanvas canvas, int xOffset, int yOffset, BlockPos pos, Direction direction, int rotation, boolean glowing, boolean isClickable) {
            final int finalXOffset = xOffset;
            final int finalYOffset = yOffset;
            int x, y, z;

            switch (rotation) {
                case 1 -> {
                    x = xOffset;
                    xOffset = -yOffset;
                    yOffset = x;
                }
                case 2 -> {
                    xOffset = -xOffset;
                    yOffset = -yOffset;
                }
                case 3 -> {
                    x = -xOffset;
                    xOffset = yOffset;
                    yOffset = x;
                }
            }


            if (direction.getAxis().isHorizontal()) {
                x = xOffset * direction.getOffsetZ();
                y = -yOffset;
                z = -xOffset * direction.getOffsetX();
            } else {
                x = xOffset;
                y = 0;
                z = yOffset * direction.getOffsetY();
            }

            var entityId = MapIdManager.requestEntityId();
            var uuid = UUID.randomUUID();
            var spawnPacket = new EntitySpawnS2CPacket(entityId, uuid,
                    pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0f, 0f,
                    glowing ? EntityType.GLOW_ITEM_FRAME : EntityType.ITEM_FRAME,
                    direction.getId(), Vec3d.ZERO);


            var trackerPacket = createClass(EntityTrackerUpdateS2CPacket.class);
            ((EntityTrackerUpdateS2CPacketAccessor) trackerPacket).setId(entityId);
            ((EntityTrackerUpdateS2CPacketAccessor) trackerPacket).setTrackedValues(
                    List.of(
                            new DataTracker.Entry<>(ItemFrameEntityAccessor.getItemStack(), canvas.asStack()),
                            new DataTracker.Entry<>(ItemFrameEntityAccessor.getRotation(), rotation),
                            new DataTracker.Entry<>(EntityAccessor.getFlags(), (byte) (1 << 5))
                    )
            );
            EntitySpawnS2CPacket entitySpawn2;
            EntityTrackerUpdateS2CPacket trackerPacket2;
            int entityId2;
            if (isClickable) {
                entityId2 = MapIdManager.requestEntityId();

                entitySpawn2 = new EntitySpawnS2CPacket(entityId2, UUID.randomUUID(),
                        pos.getX() + x + 0.5 + -direction.getOffsetX() * 0.92, pos.getY() + y + -direction.getOffsetY() * 0.92, pos.getZ() + z + 0.5 + -direction.getOffsetZ() * 0.92, 0f, 0f,
                        EntityType.FALLING_BLOCK,
                        Block.getRawIdFromState(Blocks.CHEST.getDefaultState()), Vec3d.ZERO);

                trackerPacket2 = createClass(EntityTrackerUpdateS2CPacket.class);
                ((EntityTrackerUpdateS2CPacketAccessor) trackerPacket2).setId(entityId2);
                ((EntityTrackerUpdateS2CPacketAccessor) trackerPacket2).setTrackedValues(
                        List.of(
                                new DataTracker.Entry<>(EntityAccessor.getNoGravity(), true)
                        )
                );
            } else {
                entityId2 = -1;
                entitySpawn2 = null;
                trackerPacket2 = null;
            }

            return new Holder(entityId, entityId2, finalXOffset, finalYOffset, UUID.randomUUID(), spawnPacket, trackerPacket, entitySpawn2,  trackerPacket2);
        }
    }


    private static <T> T createClass(Class<T> clazz) {
        try {
            return (T) UnsafeAccess.UNSAFE.allocateInstance(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
