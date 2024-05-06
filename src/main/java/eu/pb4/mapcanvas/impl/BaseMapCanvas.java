package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.api.core.CanvasIcon;
import eu.pb4.mapcanvas.api.core.IconContainer;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BaseMapCanvas implements DrawableCanvas, IconContainer {
    protected final byte[] data = new byte[CanvasUtils.MAP_DATA_SIZE * CanvasUtils.MAP_DATA_SIZE];
    protected final Set<SimpleCanvasIcon> icons = new HashSet<>();
    private int iconId = 0;

    @Override
    public byte getRaw(int x, int y) {
        return this.data[x + y * CanvasUtils.MAP_DATA_SIZE];
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        if (x >= CanvasUtils.MAP_DATA_SIZE || y >= CanvasUtils.MAP_DATA_SIZE || x < 0 || y < 0) {
            return;
        }
        int i = x + y * CanvasUtils.MAP_DATA_SIZE;
        if (this.data[i] != color) {
            this.markPixelDirty(x, y);
            this.data[i] = color;
        }
    }

    @Override
    public int getHeight() {
        return CanvasUtils.MAP_DATA_SIZE;
    }

    @Override
    public int getWidth() {
        return CanvasUtils.MAP_DATA_SIZE;
    }

    protected void markPixelDirty(int x, int y) { }

    protected void markIconsDirty() { }

    @Override
    public Collection<CanvasIcon> getIcons() {
        return Collections.unmodifiableCollection(this.icons);
    }

    @Override
    public CanvasIcon createIcon() {
        var icon = new SimpleCanvasIcon(this.iconId++);
        this.icons.add(icon);
        return icon;
    }

    @Override
    public CanvasIcon createIcon(RegistryEntry<MapDecorationType> type, boolean visible, int x, int y, byte rotation, @Nullable Text text) {
        var icon = new SimpleCanvasIcon(this.iconId++, visible, type, x, y, rotation, text);
        this.icons.add(icon);

        if (visible) {
            this.markIconsDirty();
        }
        return icon;
    }

    @Override
    public void removeIcon(CanvasIcon icon) {
        if (icon.isVisible() && this.icons.remove(icon)) {
            this.markIconsDirty();
        }
    }

    public final class SimpleCanvasIcon implements CanvasIcon {
        public final int id;
        private Text text;
        private RegistryEntry<MapDecorationType> type = MapDecorationTypes.PLAYER;
        private int x = 0;
        private int y = 0;
        private byte rotation = 0;
        private boolean isVisible = false;

        protected SimpleCanvasIcon(int id) {
            this.id = id;
        }

        protected SimpleCanvasIcon(int id, boolean visible, RegistryEntry<MapDecorationType> type, int x, int y, byte rotation, @Nullable Text text) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.text = text;
            this.isVisible = visible;
        }

        @Override
        public RegistryEntry<MapDecorationType> getType() {
            return this.type;
        }

        @Override
        public void setType(RegistryEntry<MapDecorationType> type) {
            if (this.type != type) {
                this.type = type;
                BaseMapCanvas.this.markIconsDirty();
            }
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public byte getRotation() {
            return this.rotation;
        }

        @Override
        public void move(int x, int y, byte rotation) {
            if (this.x != x || this.y != y || this.rotation != rotation) {
                this.x = x;
                this.y = y;
                this.rotation = rotation;
                BaseMapCanvas.this.markIconsDirty();
            }
        }

        @Override
        public boolean isVisible() {
            return this.isVisible;
        }

        @Override
        public void setVisibility(boolean visibility) {
            if (this.isVisible != visibility) {
                this.isVisible = visibility;
                BaseMapCanvas.this.markIconsDirty();
            }
        }

        @Override
        public Text getText() {
            return this.text;
        }

        @Override
        public void setText(@Nullable Text text) {
            if (!Objects.equals(this.text, text)) {
                this.text = text;
                BaseMapCanvas.this.markIconsDirty();
            }
        }

        @Override
        public DrawableCanvas getOwningCanvas() {
            return BaseMapCanvas.this;
        }
    }
}
