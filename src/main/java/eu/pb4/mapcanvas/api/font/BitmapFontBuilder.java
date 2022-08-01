package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.BitmapFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class BitmapFontBuilder {
    private final Int2ObjectMap<BitmapFont.Glyph> characters = new Int2ObjectOpenHashMap<>();
    private BitmapFont.Glyph defaultGlyph = BitmapFont.Glyph.INVALID;

    private final List<String> authors = new ArrayList<>();
    private String description = null;
    private String name = "unnamed";

    private BitmapFontBuilder() {}

    public static BitmapFontBuilder create() {
        return new BitmapFontBuilder();
    }

    /**
     * Adds an glyph to font
     *
     * @param character character to display as
     * @param glyph
     * @return self
     */
    public BitmapFontBuilder put(int character, Glyph glyph) {
        this.characters.put(character, glyph.toGlyph());
        return this;
    }

    /**
     * Sets default/undefinec glyph
     *
     * @param glyph
     * @return self
     */
    public BitmapFontBuilder defaultGlyph(Glyph glyph) {
        this.defaultGlyph = glyph.toGlyph();
        return this;
    }

    public BitmapFontBuilder addAuthor(String author) {
        this.authors.add(author);
        return this;
    }

    public BitmapFontBuilder setDescription(@Nullable String description) {
        this.description = description;
        return this;
    }

    public BitmapFontBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Builds a font
     */
    public CanvasFont build() {
        return new BitmapFont(this.defaultGlyph, this.characters, CanvasFont.Metadata.create(this.name, this.authors, this.description));
    }

    /**
     * Most basic representation of character
     */
    public static final class Glyph {
        private final int width;
        private final int height;
        private int logicalHeight = 8;
        private int ascend = 7;
        private boolean[] texture;
        private int fontWidth = 0;

        private Glyph(int width, int height) {
            this.width = width;
            this.height = height;
            this.texture = new boolean[this.width * this.height];
        }

        public static Glyph of(int width, int height) {
            return new Glyph(width, height);
        }

        /**
         * Logical height used for rendering
         * @param height
         * @return
         */
        public Glyph logicalHeight(int height) {
            this.logicalHeight = height;
            return this;
        }

        /**
         * Forces character to be a specific width
         * @param width width
         * @return
         */
        public Glyph charWidth(int width) {
            this.fontWidth = width;
            return this;
        }

        /**
         * Allows to set vertical offset of glyph
         * @param ascend
         * @return
         */
        public Glyph ascend(int ascend) {
            this.ascend = ascend;
            return this;
        }

        /**
         * Sets pixel as non-transparent
         *
         * @param x
         * @param y
         * @return
         */
        public Glyph set(int x, int y) {
            return this.set(x, y, true);
        }

        /**
         * Sets pixel to value
         *
         * @param x
         * @param y
         * @param value, true to set and false to remove
         * @return
         */
        public Glyph set(int x, int y, boolean value) {
            this.texture[x + y * this.width] = value;
            this.fontWidth = Math.max(this.fontWidth, x);
            return this;
        }

        protected BitmapFont.Glyph toGlyph() {
            return new BitmapFont.Glyph(this.width, this.height, this.ascend, this.fontWidth, this.logicalHeight, Arrays.copyOf(this.texture, this.texture.length));
        }
    }
}
