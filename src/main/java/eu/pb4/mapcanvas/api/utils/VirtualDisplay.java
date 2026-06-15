package eu.pb4.mapcanvas.api.utils;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.impl.MapIdManager;
import eu.pb4.mapcanvas.impl.PlayerInterface;
import eu.pb4.mapcanvas.mixin.EntityAccessor;
import eu.pb4.mapcanvas.mixin.ItemFrameEntityAccessor;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
    private final Set<ServerPlayer> players = new HashSet<>();
    private final boolean invisible;
    private final ClickDetection clickDetection;
    private final IntList clickableIds = new IntArrayList();
    private AABB box;

    protected VirtualDisplay(BlockPos pos, boolean glowing, Direction direction, int rotation, boolean invisible, ClickDetection clickDetection, DisplayInteractionCallback callback) {
        this.glowing = glowing;
        this.pos = pos;
        this.direction = direction;
        this.rotation = rotation;
        this.invisible = invisible;
        this.interactionCallback = callback;
        this.clickDetection = clickDetection;
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

    public final void addPlayer(ServerPlayer player) {
        if (!player.hasDisconnected() && !this.ids.isEmpty()) {
            var list = new ArrayList<Packet<? super ClientGamePacketListener>>();
            for (var holder : this.holders) {
                list.add(holder.spawnPacket);
                list.add(holder.trackerPacket);
            }
            player.connection.send(new ClientboundBundlePacket(list));
            this.players.add(player);
            ((PlayerInterface) player).mapcanvas_addDisplay(this.clickableIds, this);
        }
    }

    private AABB getBoundingBox() {
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
            this.box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
        return this.box;
    }

    public final void removePlayer(ServerPlayer player) {
        if (!player.hasDisconnected() && !this.ids.isEmpty()) {
            player.connection.send(new ClientboundRemoveEntitiesPacket(this.ids));
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
    public final void interactAt(ServerPlayer player, int id, @Nullable Vec3 pos, InteractionHand hand, ClickType type) {
        if (this.interactionCallback != null && hand == InteractionHand.MAIN_HAND) {
            var holder = this.clickableHolderById.get(id);

            if (holder == null) {
                return;
            }

            if (this.clickDetection == ClickDetection.RAYCAST) {
                var maxDistance = player.entityInteractionRange();
                var maxDistanceSqr = maxDistance*maxDistance;
                Vec3 min = player.getEyePosition(0);
                Vec3 rotVec = player.getViewVector(1.0F);
                Vec3 max = min.add(rotVec.x * maxDistance, rotVec.y * maxDistance, rotVec.z * maxDistance);
                AABB box2 = this.getBoundingBox();
                if (this.invisible) {
                    box2 = box2.move(this.direction.getStepX() * -0.0625, this.direction.getStepY() * -0.0625,
                            this.direction.getStepZ() * -0.0625);
                }
                Optional<Vec3> optional = box2.clip(min, max);
                if (optional.isPresent()) {
                    Vec3 vec3d2 = optional.get();
                    double f = min.distanceToSqr(vec3d2);
                    if (f < maxDistanceSqr || maxDistanceSqr == 0.0D) {
                        pos = vec3d2.subtract(box2.getCenter());
                    }
                } else {
                    return;
                }
            }

            if (pos == null) {
                pos = Vec3.ZERO;
            }

            var width = ((this.rotation == 1 || this.rotation == 3) ? this.getHeight() : this.getWidth());
            var height = ((this.rotation == 1 || this.rotation == 3) ? this.getWidth() : this.getHeight());

            int tmp, x, y;

            if (this.clickDetection == ClickDetection.ENTITY) {
                if (this.direction.getAxis() != Direction.Axis.Y) {
                    var dir = this.direction.getCounterClockWise();
                    x = (int) (holder.xOffset + ((pos.x * dir.getStepX() + pos.z * dir.getStepZ()) / 0.0625f) * 8 + 4);
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
                    var dir = this.direction.getCounterClockWise();
                    x = (int) ((pos.x * dir.getStepX() + pos.z * dir.getStepZ()) * 128) + width * 64;
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
        void onClick(ServerPlayer player, ClickType type, int x, int y);
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

        public Builder rotation(Rotation rotation) {
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

    protected record Holder(int entityId, int xOffset, int yOffset, Vec3 pos, AABB box, UUID uuid,
                            Packet<ClientGamePacketListener> spawnPacket,
                            Packet<ClientGamePacketListener> trackerPacket) {
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
                x = xOffset * direction.getStepZ();
                y = -yOffset;
                z = -xOffset * direction.getStepX();
            } else {
                x = xOffset;
                y = 0;
                z = yOffset * direction.getStepY();
            }

            var entityId = MapIdManager.requestEntityId();
            var uuid = UUID.randomUUID();
            var entPos = Vec3.atCenterOf(pos).add(x, y, z);

            var spawnPacket = new ClientboundAddEntityPacket(entityId, uuid,
                    entPos.x(), entPos.y(), entPos.z(), 0f, 0f,
                    glowing ? EntityTypes.GLOW_ITEM_FRAME : EntityTypes.ITEM_FRAME,
                    direction.get3DDataValue(), Vec3.ZERO, 0);


            var trackerPacket = new ClientboundSetEntityDataPacket(entityId, List.of(
                    SynchedEntityData.DataValue.create(ItemFrameEntityAccessor.getItemStack(), canvas.asStack()),
                    SynchedEntityData.DataValue.create(ItemFrameEntityAccessor.getRotation(), rotation),
                    SynchedEntityData.DataValue.create(EntityAccessor.getFlags(), (byte) ((visible ? 1 : 0) << 5))
            ));

            Vec3 boxCenter = entPos.relative(direction, -0.46875);
            var axis = direction.getAxis();
            var dx = axis == Direction.Axis.X ? 0.0625 : 1;
            var dy = axis == Direction.Axis.Y ? 0.0625 : 1;
            var dz = axis == Direction.Axis.Z ? 0.0625 : 1;

            return new Holder(entityId, finalXOffset, finalYOffset, entPos, AABB.ofSize(boxCenter, dx, dy, dz), UUID.randomUUID(), spawnPacket, trackerPacket);
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
