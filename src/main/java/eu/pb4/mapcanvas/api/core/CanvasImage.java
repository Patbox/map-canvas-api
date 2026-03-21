package eu.pb4.mapcanvas.api.core;

import com.google.gson.JsonParser;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.impl.image.FloydSteinbergDither;
import eu.pb4.mapcanvas.impl.image.RawImage;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Minimal implementation of DrawableCanvas, used to store images independent of maps
 */
public final class CanvasImage implements DrawableCanvas, IconContainer {
    private final int width;
    private final int height;
    private final byte[] data;
    private final Set<ImageCanvasIcon> icons = new HashSet<>();
    private int iconId = 0;

    public CanvasImage(int width, int height) {
        this(width, height, new byte[width * height]);
    }

    protected CanvasImage(int width, int height, byte[] data) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public static CanvasImage from(BufferedImage image) {
        return from(image, CanvasUtils.ColorMapper.DEFAULT);
    }

    public static CanvasImage from(BufferedImage image, CanvasUtils.ColorMapper mapper) {
        var width = image.getWidth();
        var height = image.getHeight();

        var canvas = new CanvasImage(width, height);
        var pixels = width * height;


        var type = image.getType();
        if (type >= BufferedImage.TYPE_3BYTE_BGR && type <= BufferedImage.TYPE_4BYTE_ABGR_PRE || type == BufferedImage.TYPE_BYTE_BINARY || type == BufferedImage.TYPE_CUSTOM) {
            var rawImage = RawImage.convert(image);
            for (int i = 0; i < pixels; i++) {
                canvas.data[i] = mapper.getRawColor(rawImage.data()[i]);
            }
        } else {
            var buf = image.getData().getDataBuffer();
            var color = image.getColorModel();
            for (int i = 0; i < pixels; i++) {
                canvas.data[i] = mapper.getRawColor(color.getRGB(buf.getElem(i)));
            }
        }

        return canvas;
    }

    public static CanvasImage fromWithFloydSteinbergDither(BufferedImage image) {
        return fromWithFloydSteinbergDither(image, CanvasUtils.ColorMapper.DEFAULT);
    }

    public static CanvasImage fromWithFloydSteinbergDither(BufferedImage image, CanvasUtils.ColorMapper mapper) {
        var rawImage = RawImage.convert(image);
        var width = rawImage.width();
        var height = rawImage.height();

        var canvas = new CanvasImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas.set(x, y, FloydSteinbergDither.sample(rawImage, x, y, mapper));
            }
        }

        return canvas;
    }


    public static CanvasImage from(BufferedImage image, ColorResolver resolver) {
        var width = image.getWidth();
        var height = image.getHeight();

        var canvas = new CanvasImage(width, height);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvas.setRaw(x, y, resolver.getRawColor(image, x, y));
            }
        }

        return canvas;
    }

    public static CanvasImage from(MapItemSavedData state) {
        var canvas = new CanvasImage(CanvasUtils.MAP_DATA_SIZE, CanvasUtils.MAP_DATA_SIZE);

        for (int x = 0; x < CanvasUtils.MAP_DATA_SIZE; x++) {
            for (int y = 0; y < CanvasUtils.MAP_DATA_SIZE; y++) {
                canvas.setRaw(x, y, state.colors[x + y * CanvasUtils.MAP_DATA_SIZE]);
            }
        }

        for (var icon : state.getDecorations()) {
            canvas.createIcon(icon.type(), icon.x() - CanvasUtils.MAP_DATA_SIZE, icon.y() - CanvasUtils.MAP_DATA_SIZE, icon.rot(), icon.name().orElse(null));
        }

        return canvas;
    }

    @Nullable
    public static CanvasImage from(CompoundTag nbt, HolderLookup.Provider lookup) {
        try {
            if (nbt.getStringOr("DataType", "").equals("MapCanvasImage") && nbt.getIntOr("Version", 0) != 0) {
                final int width = nbt.getIntOr("Width", 0);
                final int height = nbt.getIntOr("Height", 0);

                var data = nbt.getByteArray("Data").orElse(new byte[0]);

                var image = new CanvasImage(width, height, Arrays.copyOf(data, data.length));

                for (var tmpIcon : nbt.getListOrEmpty("Icons")) {
                    if (tmpIcon instanceof CompoundTag icon) {
                        image.createIcon(
                                lookup.lookupOrThrow(Registries.MAP_DECORATION_TYPE)
                                        .get(ResourceKey.create(Registries.MAP_DECORATION_TYPE, Identifier.parse(icon.getStringOr("TypeId", ""))))
                                        .map(x -> (Holder<MapDecorationType>) x)
                                        .orElse(MapDecorationTypes.PLAYER),
                                icon.getBooleanOr("Vis", false),
                                icon.getIntOr("X", 0),
                                icon.getIntOr("Y", 0),
                                icon.getByteOr("Rot", (byte) 0),
                                icon.contains("Text")
                                        ? ComponentSerialization.CODEC.decode(lookup.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(icon.getStringOr("Text", ""))).result().orElseThrow().getFirst()
                                        : null
                        );
                    }
                }

                return image;
            } else if (nbt.contains("DataVersion") && nbt.contains("data")) {
                var version = nbt.getIntOr("DataVersion", 1343);
                if (SharedConstants.WORLD_VERSION > version) {
                    try {
                        nbt = (CompoundTag) DataFixers.getDataFixer().update(References.SAVED_DATA_MAP_DATA, new Dynamic<>(NbtOps.INSTANCE, nbt), version, SharedConstants.WORLD_VERSION).getValue();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                return from(MapItemSavedData.CODEC.decode(lookup.createSerializationContext(NbtOps.INSTANCE), nbt).getOrThrow().getFirst());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte getRaw(int x, int y) {
        if (x >= this.width || y >= this.height || x < 0 || y < 0) {
            return 0;
        }

        return this.data[x + y * this.width];
    }

    @Override
    public void setRaw(int x, int y, byte color) {
        if (x >= this.width || y >= this.height || x < 0 || y < 0) {
            return;
        }

        this.data[x + y * this.width] = color;
    }

    @Override
    public void fillRaw(byte color) {
        Arrays.fill(this.data, color);
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
    public Collection<CanvasIcon> getIcons() {
        return Collections.unmodifiableCollection(this.icons);
    }

    @Override
    public CanvasIcon createIcon() {
        var icon = new ImageCanvasIcon(this.iconId++);
        this.icons.add(icon);
        return icon;
    }

    @Override
    public CanvasIcon createIcon(Holder<MapDecorationType> type, boolean visible, int x, int y, byte rotation, @Nullable Component text) {
        var icon = new ImageCanvasIcon(this.iconId++, visible, type, x, y, rotation, text);
        this.icons.add(icon);
        return icon;
    }

    @Override
    public void removeIcon(CanvasIcon icon) {
        this.icons.remove(icon);
    }

    public byte getRawAt(int index) {
        return this.data[index];
    }

    public void setRawAt(int index, byte data) {
        this.data[index] = data;
    }

    private final class ImageCanvasIcon implements CanvasIcon {
        public final int id;
        private Component text;
        private Holder<MapDecorationType> type = MapDecorationTypes.PLAYER;
        private int x = 0;
        private int y = 0;
        private byte rotation = 0;
        private boolean isVisible = false;

        protected ImageCanvasIcon(int id) {
            this.id = id;
        }

        protected ImageCanvasIcon(int id, boolean visible, Holder<MapDecorationType> type, int x, int y, byte rotation, @Nullable Component text) {
            this.id = id;
            this.type = type;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.text = text;
            this.isVisible = visible;
        }

        @Override
        public Holder<MapDecorationType> getType() {
            return this.type;
        }

        @Override
        public void setType(Holder<MapDecorationType> type) {
            if (this.type != type) {
                this.type = type;
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
            }
        }

        @Override
        public Component getName() {
            return this.text;
        }

        @Override
        public void setName(@Nullable Component text) {
            if (!Objects.equals(this.text, text)) {
                this.text = text;
            }
        }

        @Override
        public DrawableCanvas getOwningCanvas() {
            return CanvasImage.this;
        }
    }

    @FunctionalInterface
    public interface ColorResolver {
        ColorResolver DEFAULT = (image, x, y) -> CanvasUtils.findClosestRawColorARGB(image.getRGB(x, y));
        ColorResolver DEFAULT_BLACK_CLEAR = (image, x, y) -> {
            var color = image.getRGB(x, y);
            return (color & 0xFFFFFF) == 0 ? 0 : CanvasUtils.findClosestRawColorARGB(color);
        };

        byte getRawColor(BufferedImage image, int x, int y);
    }
}
