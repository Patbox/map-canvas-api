package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

import java.util.List;
import java.util.Optional;

/**
 * Default representation of a font
 */
public interface CanvasFont {
    /**
     * Returns width of the text
     * @param text input text
     * @param size font size, in pixels
     * @return width of text in pixels
     */
    default int getTextWidth(String text, double size) {
        if (text.isEmpty()) {
            return 0;
        }
        int posX = 0;

        var array = text.codePoints().toArray();
        final int length = array.length - 1;

        for (int i = 0; i < length; i++) {
            posX += this.getGlyphWidth(array[i], size, 2);
        }

        return posX + this.getGlyphWidth(array[length], size, 0);
    }

    /**
     * Draws text into canvas
     *
     * @param canvas canvas to draw on
     * @param text input text
     * @param x starting x position, font will be written into right from it
     * @param y starting y position, font will be written below it
     * @param size font size, in pixels
     * @param color color to use
     */
    default void drawText(DrawableCanvas canvas, String text, int x, int y, double size, CanvasColor color) {
        int posX = 0;
        int posY = 0;

        for (var character : text.codePoints().toArray()) {
            if (character == '\n') {
                posY += size + 2;
                posX = 0;
            } else {
                posX += this.drawGlyph(canvas, character, x + posX, y + posY, size, 2, color);
            }
        }
    }

    /**
     * Returns width of single character (glyph)
     *
     * @param character character to check
     * @param size font size, in pixels
     * @param offset additional width for font, can be used for spacing
     * @return width of glyph
     */
    int getGlyphWidth(int character, double size, int offset);

    /**
     * Draws text into canvas
     *
     * @param canvas canvas to draw on
     * @param character
     * @param x starting x position, glyph will be written into right from it
     * @param y starting y position, glyph will be written below it
     * @param size font size, in pixels
     * @param offset additional width for font, can be used for spacing
     * @param color color to use
     * @return width of glyph
     */
    int drawGlyph(DrawableCanvas canvas, int character, int x, int y, double size, int offset, CanvasColor color);

    /**
     * Allows to check if font contains provided character
     *
     * @param character character to check
     * @return true if it's present
     */
    boolean containsGlyph(int character);

    default Metadata getMetadata() {
        return Metadata.empty();
    }


    record Metadata(String name, List<String> authors, Optional<String> description) {
        private static Metadata EMPTY = new Metadata("unnamed", List.of(), Optional.empty());

        public String defaultedDescription() {
            return this.description.orElse("A Map Canvas Font");
        }

        public static Metadata create(String name, List<String> authors, String description) {
            return new Metadata(name, List.copyOf(authors), Optional.ofNullable(description));
        }

        public static Metadata empty() {
            return EMPTY;
        }
    }
}
