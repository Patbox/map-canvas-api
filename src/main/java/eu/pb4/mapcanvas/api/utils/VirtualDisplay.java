package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import eu.pb4.mapcanvas.mixin.EntityAccessor;
import eu.pb4.mapcanvas.mixin.InteractionEntityAccessor;
import eu.pb4.mapcanvas.mixin.ItemFrameEntityAccessor;
import eu.pb4.mapcanvas.mixin.PlayerInteractEntityC2SPacketAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.Entity;
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
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ClickType;
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
    private final TypedInteractionCallback interactionCallback;
    private final Set<ServerPlayerEntity> players = new HashSet<>();
    private final boolean invisible;
    private final ClickDetection clickDetection;
    private final IntList clickableIds = new IntArrayList();
    private Box box;

    protected VirtualDisplay(BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, TypedInteractionCallback callback) {
        this.glowing = glowing;
        this.pos = pos;
        this.direction = direction;
        this.rotation = rotation;
        this.invisible = invisible;
        this.interactionCallback = callback;
        this.clickDetection = clickDetection;
    }

    @Deprecated
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

    @Deprecated
    public static VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing) {
        return of(canvas, pos, direction, rotation, glowing, null);
    }

    @Deprecated
    public static VirtualDisplay of(PlayerCanvas canvas, BlockPos pos, Direction direction, int rotation, boolean glowing, @Nullable TypedInteractionCallback callback) {
        if (canvas instanceof CombinedPlayerCanvas combinedCanvas) {
            return new Combined(combinedCanvas, pos, glowing, direction, Math.abs(rotation % 4), true, callback != null ? ClickDetection.ENTITY : ClickDetection.NONE, callback);
        } else {
            return new Single(canvas, pos, glowing, direction, Math.abs(rotation % 4), true, callback != null ? ClickDetection.ENTITY : ClickDetection.NONE, callback);
        }
    }

    @Deprecated
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
        if (this.direction.getAxis() == Direction.Axis.Y) {
            //var d = 0.0625 / 2;
            var entityHeight = 0.0626f;

            double x = this.pos.getX() + 0.5D;
            var y = this.pos.getY();
            double z = this.pos.getZ() + 0.5D;
            var width = ((this.rotation == 1 || this.rotation == 3) ? this.getHeight() : this.getWidth());
            var height = ((this.rotation == 1 || this.rotation == 3) ? this.getWidth() : this.getHeight());

            if (this.rotation == 1) {
                x -= width - 1;
                if (this.direction == Direction.DOWN) {
                    z -= height - 1;
                }
            } else if (this.rotation == 2) {
                x -= width - 1;
                if (this.direction == Direction.UP) {
                    z -= height - 1;
                }
            } else if ((this.rotation == 3 && this.direction == Direction.UP) || (this.rotation == 0 && this.direction == Direction.DOWN)) {
                z -= height - 1;
            }

            var baseVec = new Vec3d(x, y - entityHeight + (this.direction == Direction.DOWN ? 1 : entityHeight), z);
            for (int xi = 0; xi < width; xi++) {
                for (int yi = 0; yi < height; yi++) {
                    var entityId = MapIdManager.requestEntityId();
                    var uuid = UUID.randomUUID();
                    var vec = baseVec.offset(Direction.SOUTH, yi).offset(Direction.EAST, xi);

                    var spawnPacket = new EntitySpawnS2CPacket(entityId, uuid,
                            vec.x, vec.y, vec.z, 0f, 0f,
                            EntityType.INTERACTION,
                            direction.getIndex(), Vec3d.ZERO, 0);


                    var trackerPacket = new EntityTrackerUpdateS2CPacket(entityId, List.of(
                            DataTracker.SerializedEntry.of(InteractionEntityAccessor.getHEIGHT(), entityHeight),
                            DataTracker.SerializedEntry.of(InteractionEntityAccessor.getWIDTH(), 1f),
                            DataTracker.SerializedEntry.of(EntityAccessor.getFlags(), (byte) (1 << 5))
                    ));

                    float f = 0.5f;
                    var box = new Box(vec.x - (double)f, vec.y, vec.z - (double)f, vec.x + (double)f, vec.y + (double)entityHeight, vec.z + (double)f);

                    var holder = new Holder(entityId, xi * 128, yi * 128, vec, box, uuid, spawnPacket, trackerPacket);
                    this.holders.add(holder);
                    this.clickableIds.add(entityId);
                    this.clickableHolderById.put(holder.entityId, holder);
                    this.ids.add(entityId);
                }
            }
        } else {
            var d = 0.0625 / 2;
            var count = ((this.rotation == 1 || this.rotation == 3) ? this.getHeight() : this.getWidth()) / 0.0625;
            var height = ((this.rotation == 1 || this.rotation == 3) ? this.getWidth() : this.getHeight());

            var dir = this.direction.rotateYCounterclockwise();
            double x = (double) this.pos.getX() + 0.5D - (double) this.direction.getOffsetX() * 0.46875D - dir.getOffsetX() * 0.5;
            var y = this.pos.getY();
            double z = (double) this.pos.getZ() + 0.5D - (double) this.direction.getOffsetZ() * 0.46875D - dir.getOffsetZ() * 0.5;

            if (this.rotation == 1) {
                x -= dir.getOffsetX() * (this.getHeight() - 1);
                z -= dir.getOffsetZ() * (this.getHeight() - 1);
            } else if (this.rotation == 2) {
                x -= dir.getOffsetX() * (this.getWidth() - 1);
                y += height - 1;
                z -= dir.getOffsetZ() * (this.getWidth() - 1);
            } else if (this.rotation == 3) {
                y += height - 1;
            }

            var baseVec = new Vec3d(x, y - height + 1, z);
            for (int i = 0; i < count; i++) {
                var entityId = MapIdManager.requestEntityId();
                var uuid = UUID.randomUUID();
                var vec = baseVec
                        .offset(dir, d + i * 0.0625);

                var spawnPacket = new EntitySpawnS2CPacket(entityId, uuid,
                        vec.x, vec.y, vec.z, 0f, 0f,
                        EntityType.INTERACTION,
                        direction.getIndex(), Vec3d.ZERO, 0);


                var trackerPacket = new EntityTrackerUpdateS2CPacket(entityId, List.of(
                        DataTracker.SerializedEntry.of(InteractionEntityAccessor.getHEIGHT(), (float) height),
                        DataTracker.SerializedEntry.of(InteractionEntityAccessor.getWIDTH(), 0.0626f),
                        DataTracker.SerializedEntry.of(EntityAccessor.getFlags(), (byte) (1 << 5))
                ));

                float f = 0.0626f / 2;
                var box = new Box(vec.x - (double)f, vec.y, vec.z - (double)f, vec.x + (double)f, vec.y + (double)height, vec.z + (double)f);

                var holder = new Holder(entityId, i * 8, 0, vec, box, uuid, spawnPacket, trackerPacket);
                this.holders.add(holder);
                this.clickableIds.add(entityId);
                this.clickableHolderById.put(holder.entityId, holder);
                this.ids.add(entityId);
            }
        }
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract PlayerCanvas getCanvas();

    @ApiStatus.Internal
    public final void interactAt(ServerPlayerEntity player, int id, @Nullable Vec3d pos, Hand hand, boolean isAttack) {
        if (this.interactionCallback != null && hand == Hand.MAIN_HAND) {
            var holder = this.clickableHolderById.get(id);

            if (holder == null) {
                return;
            }

            if (this.clickDetection == ClickDetection.RAYCAST) {
                var maxDistance = 9;
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
                    y = (int) ((-pos.y) * 128) + height * 64;;
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

            this.interactionCallback.onClick(player, isAttack ? ClickType.LEFT : ClickType.RIGHT, x, y);
        }
    }

    @ApiStatus.Internal
    public final void handleInteractionPacket(PlayerInteractEntityC2SPacket packet, ServerPlayerEntity player) {
        var id = ((PlayerInteractEntityC2SPacketAccessor) packet).getEntityId();

        packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
            @Override
            public void interact(Hand hand) {
            }

            @Override
            public void interactAt(Hand hand, Vec3d pos) {
                VirtualDisplay.this.interactAt(player, id, pos, hand, false);
            }

            @Override
            public void attack() {
                VirtualDisplay.this.interactAt(player, id, null, Hand.MAIN_HAND, true);
            }
        });
    }

    protected enum ClickDetection {
        NONE,
        ENTITY,
        RAYCAST
    }

    public interface TypedInteractionCallback {
        void onClick(ServerPlayerEntity player, ClickType type, int x, int y);
    }

    @Deprecated
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
        private TypedInteractionCallback callback;

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

        public Builder type(boolean invisible, boolean glowing) {
            this.invisible = invisible;
            this.glowing = glowing;
            return this;
        }

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

        private Single(PlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, TypedInteractionCallback callback) {
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

        private Combined(CombinedPlayerCanvas canvas, BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, TypedInteractionCallback callback) {
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
            var spawnPacket = new EntitySpawnS2CPacket(entityId, uuid,
                    pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0f, 0f,
                    glowing ? EntityType.GLOW_ITEM_FRAME : EntityType.ITEM_FRAME,
                    direction.getIndex(), Vec3d.ZERO, 0);


            var trackerPacket = new EntityTrackerUpdateS2CPacket(entityId, List.of(
                    DataTracker.SerializedEntry.of(ItemFrameEntityAccessor.getItemStack(), canvas.asStack()),
                    DataTracker.SerializedEntry.of(ItemFrameEntityAccessor.getRotation(), rotation),
                    DataTracker.SerializedEntry.of(EntityAccessor.getFlags(), (byte) ((visible ? 1 : 0) << 5))
            ));

            return new Holder(entityId, finalXOffset, finalYOffset, Vec3d.ZERO, Box.from(Vec3d.ZERO), UUID.randomUUID(), spawnPacket, trackerPacket);
        }
    }
}
