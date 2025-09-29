package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import eu.pb4.mapcanvas.mixin.EntityAccessor;
import eu.pb4.mapcanvas.mixin.ItemFrameEntityAccessor;
import eu.pb4.mapcanvas.mixin.PlayerInteractEntityC2SPacketAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import java.util.*;

public sealed abstract class VirtualDisplay permits VirtualDisplay.Combined, VirtualDisplay.Single {
    private final IntList ids = new IntArrayList();
    private final List<Holder> holders = new ArrayList<>();
    private final Int2ObjectMap<Holder> holderById = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Holder> clickableHolderById = new Int2ObjectOpenHashMap<>();
    private final boolean glowing;
    private final BlockPos pos;
    private final Direction direction;
    private final int rotation;
    private final DisplayInteractionCallback interactionCallback;
    private final Set<ServerPlayerEntity> players = new HashSet<>();
    private final boolean invisible;
    private final ClickDetection clickDetection;
    private final IntList clickableIds = new IntArrayList();
    private Box box;

    protected VirtualDisplay(BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, DisplayInteractionCallback callback) {
        this.glowing = glowing;
        this.pos = pos;
        this.direction = direction;
        this.rotation = rotation;
        this.invisible = invisible;
        this.interactionCallback = callback;
        this.clickDetection = clickDetection;
    }

    @Deprecated(forRemoval = true)
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(PlayerCanvas canvas, BlockPos pos, Direction direction) {
        return new Builder().canvas(canvas).pos(pos).direction(direction);
    }

    private static <T> T createClass(Class<T> clazz) {
        try {
            return (T) UnsafeAccess.UNSAFE.allocateInstance(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated(forRemoval = true)
    public static VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing) {
        return of(canvas, pos, direction, rotation, glowing, null);
    }

    @Deprecated(forRemoval = true)
    public static VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing, @Nullable TypedInteractionCallback callback) {
        if (canvas instanceof CombinedPlayerCanvas combinedCanvas) {
            return new Combined(combinedCanvas, pos, glowing, direction, Math.abs(rotation % 4), true, callback != null ? ClickDetection.ENTITY : ClickDetection.NONE, callback);
        } else {
            return new Single(canvas, pos, glowing, direction, Math.abs(rotation % 4), true, callback != null ? ClickDetection.ENTITY : ClickDetection.NONE, callback);
        }
    }

    @Deprecated(forRemoval = true)
    public static VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing, @Nullable InteractionCallback callback) {
        return of(canvas, pos, direction, rotation, glowing, (TypedInteractionCallback) callback);
    }

    public final void addPlayer(ServerPlayerEntity player) {
        if (!player.isDisconnected() && !this.ids.isEmpty()) {
            var list = new ArrayList<Packet<? super ClientPlayPacketListener>>();
            for (var holder : this.holders) {
                list.add(holder.spawnPacket);
                list.add(holder.trackerPacket);
            }
            player.networkHandler.sendPacket(new BundleS2CPacket(list));
            this.players.add(player);
            ((PlayerInterface) player).mapcanvas_addDisplay(this.clickableIds, this);
        }
    }

    private Box getBox() {
        if (this.box == null && !this.clickableHolderById.isEmpty()) {
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double minZ = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            double maxZ = Double.NEGATIVE_INFINITY;
            for (var x : clickableHolderById.values()) {
                minX = Math.min(minX, x.box.minX);
                minY = Math.min(minY, x.box.minY);
                minZ = Math.min(minZ, x.box.minZ);
                maxX = Math.max(maxX, x.box.maxX);
                maxY = Math.max(maxY, x.box.maxY);
                maxZ = Math.max(maxZ, x.box.maxZ);
            }
            this.box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return this.box;
    }

    public final void removePlayer(ServerPlayerEntity player) {
        if (!player.isDisconnected() && !this.ids.isEmpty()) {
            player.networkHandler.sendPacket(new EntitiesDestroyS2CPacket(this.ids));
        }
        this.players.remove(player);
        ((PlayerInterface) player).mapcanvas_removeDisplay(this.clickableIds, this);
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
        var holder = Holder.ofItemFrame(canvas, xOffset, yOffset, this.pos, this.direction, this.rotation, this.glowing, this.invisible);
        this.holders.add(holder);
        this.ids.add(holder.entityId);
        this.holderById.put(holder.entityId, holder);
    }

    protected void createInteractions() {
        this.box = null;
        this.clickableIds.clear();
        this.clickableHolderById.clear();
        this.clickableHolderById.putAll(this.holderById);
        this.clickableIds.addAll(this.clickableHolderById.keySet());
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract PlayerCanvas getCanvas();

    @ApiStatus.Internal
    public final void interactAt(ServerPlayerEntity player, int id, @Nullable Vec3d pos, Hand hand, ClickType type) {
        if (this.interactionCallback != null && hand == Hand.MAIN_HAND) {
            var holder = this.clickableHolderById.get(id);

            if (holder == null) {
                return;
            }

            if (this.clickDetection == ClickDetection.RAYCAST) {
                var maxDistance = player.getEntityInteractionRange();
                var maxDistanceSqr = maxDistance*maxDistance;
                Vec3d min = player.getCameraPosVec(0);
                Vec3d rotVec = player.getRotationVec(1.0F);
                Vec3d max = min.add(rotVec.x * maxDistance, rotVec.y * maxDistance, rotVec.z * maxDistance);
                Box box2 = this.getBox();
                if (this.invisible) {
                    box2 = box2.offset(this.direction.getOffsetX() * -0.0625, this.direction.getOffsetY() * -0.0625,
                            this.direction.getOffsetZ() * -0.0625);
                }
                Optional<Vec3d> optional = box2.raycast(min, max);
                if (optional.isPresent()) {
                    Vec3d vec3d2 = optional.get();
                    double f = min.squaredDistanceTo(vec3d2);
                    if (f < maxDistanceSqr || maxDistanceSqr == 0.0D) {
                        pos = vec3d2.subtract(box2.getCenter());
                    }
                } else {
                    return;
                }
            }

            if (pos == null) {
                pos = Vec3d.ZERO;
            }

            var width = ((this.rotation == 1 || this.rotation == 3) ? this.getHeight() : this.getWidth());
            var height = ((this.rotation == 1 || this.rotation == 3) ? this.getWidth() : this.getHeight());

            int tmp, x, y;

            if (this.clickDetection == ClickDetection.ENTITY) {
                if (this.direction.getAxis() != Direction.Axis.Y) {
                    var dir = this.direction.rotateYCounterclockwise();
                    x = (int) (holder.xOffset + ((pos.x * dir.getOffsetX() + pos.z * dir.getOffsetZ()) / 0.0625f) * 8 + 4);
                    y = (int) (holder.yOffset + (height - pos.y) * 128);
                } else {
                    x = (int) (holder.xOffset + (pos.x) * 128) + 64;
                    y = (int) (holder.yOffset + (pos.z) * 128) + 64;

                    if (this.direction == Direction.DOWN) {
                        y = height * 128 - y;
                    }
                }
            } else {
                if (this.direction.getAxis() != Direction.Axis.Y) {
                    var dir = this.direction.rotateYCounterclockwise();
                    x = (int) ((pos.x * dir.getOffsetX() + pos.z * dir.getOffsetZ()) * 128) + width * 64;
                    y = (int) ((-pos.y) * 128) + height * 64;
                } else {
                    x = (int) ((pos.x) * 128) + width * 64;
                    y = (int) ((pos.z) * 128) + height * 64;

                    if (this.direction == Direction.DOWN) {
                        y = height * 128 - y;
                    }
                }
            }

            if (this.rotation == 1) {
                tmp = x;
                x = y;
                y = width * 128 - tmp;
            } else if (this.rotation == 2) {
                x = width * 128 - x;
                y = height * 128 - y;
            } else if (this.rotation == 3) {
                tmp = y;
                y = x;
                x = height * 128 - tmp;
            }

            this.interactionCallback.onClick(player, type, x, y);
        }
    }

    protected enum ClickDetection {
        NONE,
        ENTITY,
        RAYCAST
    }

    public interface DisplayInteractionCallback {
        void onClick(ServerPlayerEntity player, ClickType type, int x, int y);
    }

    @Deprecated(forRemoval = true)
    public interface TypedInteractionCallback extends DisplayInteractionCallback {
        void onClick(ServerPlayerEntity player, net.minecraft.util.ClickType type, int x, int y);

        @Override
        default void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
            this.onClick(player, type == ClickType.RIGHT ? net.minecraft.util.ClickType.RIGHT : net.minecraft.util.ClickType.LEFT, x, y);
        }
    }

    @Deprecated(forRemoval = true)
    public interface InteractionCallback extends TypedInteractionCallback {
        void onClick(ServerPlayerEntity player, int x, int y);

        @Override
        default void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
            if (type == ClickType.RIGHT) {
                this.onClick(player, x, y);
            }
        }
    }

    public static final class Builder {
        private PlayerCanvas canvas;
        private BlockPos pos;
        private Direction direction;
        private boolean glowing;
        private boolean invisible;
        private int rotation = 0;
        private ClickDetection clickDetection = ClickDetection.NONE;
        private DisplayInteractionCallback callback;

        private Builder() {
        }

        public Builder canvas(PlayerCanvas canvas) {
            this.canvas = canvas;
            return this;
        }

        public Builder pos(BlockPos pos) {
            this.pos = pos;
            return this;
        }

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder rotation(BlockRotation rotation) {
            this.rotation = rotation.rotate(0, 4);
            return this;
        }

        public Builder glowing() {
            this.glowing = true;
            return this;
        }

        public Builder glowing(boolean value) {
            this.glowing = value;
            return this;
        }

        public Builder invisible(boolean value) {
            this.invisible = value;
            return this;
        }

        public Builder invisible() {
            this.invisible = true;
            return this;
        }

        public Builder raycast() {
            this.clickDetection = ClickDetection.RAYCAST;
            return this;
        }

        public Builder raycastClickDetection() {
            this.clickDetection = ClickDetection.RAYCAST;
            return this;
        }

        public Builder entityClickDetection() {
            this.clickDetection = ClickDetection.ENTITY;
            return this;
        }

        public Builder type(boolean invisible, boolean glowing) {
            this.invisible = invisible;
            this.glowing = glowing;
            return this;
        }
        public Builder interactionCallback(DisplayInteractionCallback callback) {
            this.callback = callback;
            if (this.clickDetection == ClickDetection.NONE) {
                this.clickDetection = ClickDetection.RAYCAST;
            }
            return this;
        }

        @Deprecated(forRemoval = true)
        public Builder callback(TypedInteractionCallback callback) {
            this.callback = callback;
            if (this.clickDetection == ClickDetection.NONE) {
                this.clickDetection = ClickDetection.ENTITY;
            }
            return this;
        }

        public VirtualDisplay build() {
            if (canvas instanceof CombinedPlayerCanvas combinedCanvas) {
                return new Combined(combinedCanvas, this.pos, this.glowing, this.direction, this.rotation, this.invisible, this.clickDetection, this.callback);
            } else {
                return new Single(this.canvas, this.pos, this.glowing, this.direction, this.rotation, this.invisible, this.clickDetection, this.callback);
            }
        }
    }

    protected static final class Single extends VirtualDisplay {
        private final PlayerCanvas canvas;

        private Single(PlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, DisplayInteractionCallback callback) {
            super(pos, glowing, direction, rotation, invisible, clickDetection, callback);
            this.canvas = canvas;
            this.addHolder(this.canvas, 0, 0);
            if (clickDetection != ClickDetection.NONE) {
                this.createInteractions();
            }
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

        private Combined(CombinedPlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, DisplayInteractionCallback callback) {
            super(pos, glowing, direction, rotation, invisible, clickDetection, callback);
            this.width = canvas.getSectionsWidth();
            this.height = canvas.getSectionsHeight();
            this.canvas = canvas;


            for (int x = 0; x < this.width; x++) {
                for (int y = 0; y < this.height; y++) {
                    this.addHolder(this.canvas.getSubCanvas(x, y), x, y);
                }
            }

            if (clickDetection != ClickDetection.NONE) {
                this.createInteractions();
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

    protected record Holder(int entityId, int xOffset, int yOffset, Vec3d pos, Box box, UUID uuid,
                            Packet<ClientPlayPacketListener> spawnPacket,
                            Packet<ClientPlayPacketListener> trackerPacket) {
        public static Holder ofItemFrame(PlayerCanvas canvas, int xOffset, int yOffset, BlockPos pos, Direction direction, int rotation, boolean glowing, boolean visible) {
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
            var entPos = Vec3d.ofCenter(pos).add(x, y, z);

            var spawnPacket = new EntitySpawnS2CPacket(entityId, uuid,
                    entPos.getX(), entPos.getY(), entPos.getZ(), 0f, 0f,
                    glowing ? EntityType.GLOW_ITEM_FRAME : EntityType.ITEM_FRAME,
                    direction.getIndex(), Vec3d.ZERO, 0);


            var trackerPacket = new EntityTrackerUpdateS2CPacket(entityId, List.of(
                    DataTracker.SerializedEntry.of(ItemFrameEntityAccessor.getItemStack(), canvas.asStack()),
                    DataTracker.SerializedEntry.of(ItemFrameEntityAccessor.getRotation(), rotation),
                    DataTracker.SerializedEntry.of(EntityAccessor.getFlags(), (byte) ((visible ? 1 : 0) << 5))
            ));

            Vec3d boxCenter = entPos.offset(direction, -0.46875);
            var axis = direction.getAxis();
            var dx = axis == Direction.Axis.X ? 0.0625 : 1;
            var dy = axis == Direction.Axis.Y ? 0.0625 : 1;
            var dz = axis == Direction.Axis.Z ? 0.0625 : 1;

            return new Holder(entityId, finalXOffset, finalYOffset, entPos, Box.of(boxCenter, dx, dy, dz), UUID.randomUUID(), spawnPacket, trackerPacket);
        }
    }

    public enum ClickType {
        LEFT,
        RIGHT,
        MIDDLE,
        MIDDLE_CTRL;


        public boolean isLeft() {
            return this == LEFT;
        }

        public boolean isRight() {
            return this == RIGHT;
        }

        public boolean isMiddle() {
            return this == MIDDLE || this == MIDDLE_CTRL;
        }
    }
}
