package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.api.core.CombinedPlayerCanvas;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.core.CanvasIcon;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MultiMapCanvasImpl implements CombinedPlayerCanvas {
    private int width;
    private int height;
    private MapCanvasPart[] parts;
    private final Set<WrappedMapIcon> icons = new HashSet<>();

    private final Set<ServerPlayNetworkHandler> players = Collections.synchronizedSet(new HashSet<>());

    public MultiMapCanvasImpl(int width, int height) {
        this.resize(width, height);
    }

    @Override
    public int getIdOf(int x, int y) {
        if (x < this.width && y < this.height && x >= 0 && y >= 0) {
            return this.parts[x + y * this.width].getId();
        }
        return 0;
    }

    @Override
    public ItemStack asStackOf(int x, int y) {
        if (x < this.width && y < this.height && x >= 0 && y >= 0) {
            return this.parts[x + y * this.width].asStack();
        }
        return new ItemStack(Items.FILLED_MAP);
    }

    @Override
    @Nullable
    public PlayerCanvas getSubCanvas(int x, int y) {
        if (x < this.width && y < this.height && x >= 0 && y >= 0) {
            return this.parts[x + y * this.width];
        }
        return null;
    }

    @Override
    public int getSectionsWidth() {
        return this.width;
    }

    @Override
    public int getSectionsHeight() {
        return this.height;
    }

    public void resize(final int width, final int height) {
        var oldParts = this.parts;

        this.width = width;
        this.height = height;
        this.parts = new MapCanvasPart[width * height];

        if (oldParts != null) {
            for (var path : oldParts) {
                if (path.x < width && path.y < height) {
                    this.parts[path.x + path.y * width] = path;
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var i = x + y * width;

                if (this.parts[i] == null) {
                    var part = new MapCanvasPart(MapIdManager.requestMapId());
                    part.x = x;
                    part.y = y;
                    this.parts[i] = part;
                }
            }
        }
    }

    @Override
    public byte getRaw(int x, int y) {
        final int xI = x / CanvasUtils.MAP_DATA_SIZE;
        final int yI = y / CanvasUtils.MAP_DATA_SIZE;

        if (xI < this.width && yI < this.height && xI >= 0 && yI >= 0) {
            return this.parts[xI + yI * this.width].getRaw(x - xI * CanvasUtils.MAP_DATA_SIZE, y - yI * CanvasUtils.MAP_DATA_SIZE);
        }
        return 0;
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        final int xI = x / CanvasUtils.MAP_DATA_SIZE;
        final int yI = y / CanvasUtils.MAP_DATA_SIZE;

        if (xI < this.width && yI < this.height && xI >= 0 && yI >= 0) {
            this.parts[xI + yI * this.width].setRaw(x - xI * CanvasUtils.MAP_DATA_SIZE, y - yI * CanvasUtils.MAP_DATA_SIZE, color);
        }
    }

    @Override
    public void fillRaw(byte color) {
        for (MapCanvasPart part : this.parts) {
            part.fillRaw(color);
        }
    }

    @Override
    public int getHeight() {
        return this.height * CanvasUtils.MAP_DATA_SIZE;
    }

    @Override
    public int getWidth() {
        return this.width * CanvasUtils.MAP_DATA_SIZE;
    }

    @Override
    public CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, boolean visible, int x, int y, byte rotation, @Nullable Text text) {
        int canvasX = x / CanvasUtils.MAP_ICON_SIZE;
        int canvasY = y / CanvasUtils.MAP_ICON_SIZE;
        var icon = new WrappedMapIcon(this.getSubCanvas(canvasX, canvasY).createIcon(type, visible, x % CanvasUtils.MAP_ICON_SIZE, y % CanvasUtils.MAP_ICON_SIZE, rotation, text), canvasX, canvasY);
        this.icons.add(icon);
        return icon;
    }

    @Override
    public CanvasIcon createIcon() {
        var icon = new WrappedMapIcon(this.parts[0].createIcon(), 0, 0);
        this.icons.add(icon);
        return icon;
    }

    @Override
    public void removeIcon(CanvasIcon icon) {
        if (this.icons.remove(icon) && icon instanceof WrappedMapIcon wrappedMapIcon) {
            wrappedMapIcon.icon.remove();
        }
    }

    @Override
    public Collection<CanvasIcon> getIcons() {
        return Collections.unmodifiableCollection(this.icons);
    }

    @Override
    public boolean addPlayer(ServerPlayNetworkHandler player) {
        if (this.isDestroyed()) {
            return false;
        }

        if (this.players.add(player)) {
            for (var part : this.parts) {
                part.sendFull(player);
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayer(ServerPlayNetworkHandler player) {
        return this.players.remove(player);
    }

    @Override
    public void sendUpdates() {
        for (var part : this.parts) {
            part.sendUpdates();
        }
    }

    @Override
    public boolean isDirty() {
        for (var parts : this.parts) {
            if (parts.isDirty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MapIdComponent getIdComponent() {
        return null;
    }

    @Override
    public void destroy() {
        if (this.isDestroyed()) {
            return;
        }

        for (var player : new ArrayList<>(this.players)) {
            this.removePlayer(player);
        }

        for (var part : this.parts) {
            part.destroy();
        }
    }

    @Override
    public boolean isDestroyed() {
       return this.parts[0].isDestroyed();
    }

    private final class WrappedMapIcon implements CanvasIcon {
        public int y;
        public int x;
        public CanvasIcon icon;

        protected WrappedMapIcon(CanvasIcon icon, int x, int y) {
            this.icon = icon;
            this.x = x;
            this.y = y;
        }

        @Override
        public RegistryEntry<MapDecorationType> getType() {
            return this.icon.getType();
        }

        @Override
        public void setType(RegistryEntry<MapDecorationType> type) {
            this.icon.setType(type);
        }

        @Override
        public int getX() {
            return this.icon.getX() + this.x * CanvasUtils.MAP_ICON_SIZE;
        }

        @Override
        public int getY() {
            return this.icon.getY() + this.y * CanvasUtils.MAP_ICON_SIZE;
        }

        @Override
        public byte getRotation() {
            return this.icon.getRotation();
        }

        @Override
        public void move(int x, int y, byte rotation) {
            int newX = x / CanvasUtils.MAP_ICON_SIZE;
            int newY = y / CanvasUtils.MAP_ICON_SIZE;

            if (this.x != newX || this.y != newY) {
                this.x = newX;
                this.y = newY;
                this.icon.remove();
                this.icon = MultiMapCanvasImpl.this.getSubCanvas(newX, newY).createIcon(this.icon.getType(), this.icon.isVisible(), x % CanvasUtils.MAP_ICON_SIZE, y % CanvasUtils.MAP_ICON_SIZE, rotation, this.icon.getText());
            } else {
                this.icon.move(x % CanvasUtils.MAP_ICON_SIZE, y % CanvasUtils.MAP_ICON_SIZE, rotation);
            }
        }

        @Override
        public boolean isVisible() {
            return this.icon.isVisible();
        }

        @Override
        public void setVisibility(boolean visibility) {
            this.icon.setVisibility(visibility);
        }

        @Override
        public @Nullable Text getText() {
            return this.icon.getText();
        }

        @Override
        public void setText(Text text) {
            this.icon.setText(text);
        }

        @Override
        public DrawableCanvas getOwningCanvas() {
            return MultiMapCanvasImpl.this;
        }
    }

    private final class MapCanvasPart extends AbstractPlayerMapCanvas {
        protected int x = 0;
        protected int y = 0;

        public MapCanvasPart(int id) {
            super(id);
        }

        @Override
        protected Collection<ServerPlayNetworkHandler> getPlayers() {
            return MultiMapCanvasImpl.this.players;
        }
    }
}
