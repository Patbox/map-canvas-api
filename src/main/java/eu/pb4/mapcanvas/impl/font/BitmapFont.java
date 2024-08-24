package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.MathHelper;

public final class BitmapFont implements CanvasFont {
    public static final BitmapFont EMPTY = new BitmapFont(Glyph.INVALID, Metadata.empty());

    public final Int2ObjectMap<Glyph> characters = new Int2ObjectOpenHashMap<>();
    public final Glyph defaultGlyph;
    private final Metadata metadata;

    public BitmapFont(Glyph defaultGlyph, Metadata metadata) {
        this.defaultGlyph = defaultGlyph;
        this.metadata = metadata;
    }

    public BitmapFont(Glyph defaultGlyph, Int2ObjectMap<Glyph> characters, Metadata metadata) {
        this.defaultGlyph = defaultGlyph;

        for (var entry : characters.int2ObjectEntrySet()) {
            this.characters.put(entry.getIntKey(), entry.getValue());
        }

        this.metadata = metadata;
    }

    public BitmapFont(Glyph defaultGlyph, Int2ObjectMap<Glyph> characters) {
        this(defaultGlyph, characters, Metadata.empty());
    }

    @Override
    public int getGlyphWidth(int character, double size, int offset) {
        var glyph = this.characters.getOrDefault(character, this.defaultGlyph);
        if (glyph.logicalHeight() == 0 || glyph.height() == 0) {
            return (int) (((glyph.fontWidth())) * (size / 8));
        }

        final double textureScale = (double) glyph.height() / glyph.logicalHeight();
        final double baseScale = size / textureScale / 8;

        return (int) (((glyph.fontWidth() + offset)) * baseScale);
    }

    @Override
    public int drawGlyph(DrawableCanvas canvas, int character, int x, int y, double size, int offset, CanvasColor color) {
        var glyph = this.characters.getOrDefault(character, this.defaultGlyph);

        if (glyph.logicalHeight() == 0 || glyph.height() == 0) {
            return (int) (((glyph.fontWidth())) * (size / 8));
        }

        final double textureScale = (double) glyph.height() / glyph.logicalHeight();
        final double baseScale = size / textureScale / 8;

        for (int fX = 0; fX < glyph.width(); fX++) {
            for (int fY = 0; fY < glyph.height(); fY++) {
                if (glyph.texture()[fX + fY * glyph.width()]) {
                    for (int lX = 0; lX < baseScale; lX++) {
                        for (int lY = 0; lY < baseScale; lY++) {
                            canvas.set(
                                    x + MathHelper.floor(fX * baseScale) + lX,
                                    y + MathHelper.floor((fY + (7 - glyph.ascend()) * textureScale) * baseScale) + lY,
                                    color
                            );
                        }
                    }
                }
            }
        }

        return (int) (((glyph.fontWidth() + offset)) * baseScale);
    }

    @Override
    public boolean containsGlyph(int character) {
        return this.characters.containsKey(character);
    }

    @Override
    public Metadata getMetadata() {
        return this.metadata;
    }

    public record Glyph(int width, int height, int ascend, int fontWidth, int logicalHeight, boolean[] texture) {
        public static final Glyph INVALID;
        public static final Glyph EMPTY = new Glyph(0, 0, 0, 0, 0, new boolean[] {});

        static  {
            var array =  new boolean[8*5];

            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 8; y++) {
                    if (x == 0 || y == 0 || x == 4 || y == 7) {
                        array[x + y * 5] = true;
                    }
                }
            }

            INVALID = new Glyph(5, 8,  7, 5,8, array);
        }
    }
}
