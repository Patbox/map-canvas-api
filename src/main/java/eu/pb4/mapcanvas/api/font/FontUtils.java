package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.*;
import net.minecraft.util.Identifier;

import java.awt.Font;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipFile;

public final class FontUtils {
    private FontUtils(){}
    /**
     * Merges multiple fonts into one, allowing to stack/fill up possible missing characters
     *
     * @param fonts fonts to merge
     * @return single font with merged characters
     */
    public static CanvasFont merge(CanvasFont... fonts) {
        return new StackedFont(fonts);
    }

    /**
     * Creates new font from vanilla definitions
     * You can stack them to fill missing entries or use vanilla json definitions
     *
     * @param identifier font's identifier
     * @param zipFile sources
     * @return Font
     */
    public static CanvasFont fromVanillaFormat(Identifier identifier, ZipFile... zipFile) {
        return VanillaFontReader.build(zipFile, identifier);
    }

    /**
     * Creates new font from vanilla definitions
     * You can stack them to fill missing entries or use vanilla json definitions
     *
     * @param identifier font's identifier
     * @param metadata font's metadata
     * @param zipFile sources
     * @return Font
     */
    public static CanvasFont fromVanillaFormat(Identifier identifier, CanvasFont.Metadata metadata, ZipFile... zipFile) {
        return VanillaFontReader.build(zipFile, metadata, identifier);
    }

    /**
     * Reads font from Map Canvas API Font format
     * @param stream stream for files/bytes of font
     * @return New canvas font
     */
    public static CanvasFont fromMapCanvasFontFormat(InputStream stream) {
        var font = RawBitmapFontSerializer.read(stream);
        return font != null ? font : BitmapFont.EMPTY;
    }

    /**
     * Creates canvas font from
     * @param font Awt Font used as a base
     * @return New canvas font
     */
    public static CanvasFont fromAwtFont(Font font) {
        return new AwtFont(font, CanvasFont.Metadata.create(font.getName(), List.of(), "A font"));
    }

    /**
     * Creates canvas font from
     * @param font Awt Font used as a base
     * @param metadata Metadata used by font
     * @return New canvas font
     */
    public static CanvasFont fromAwtFont(Font font, CanvasFont.Metadata metadata) {
        return new AwtFont(font, metadata);
    }

    /**
     * Writes font to Map Canvas API Font format
     * Only works with bitmap fonts
     *
     * @param font font to convert
     * @param stream stream it will be written to
     * @return true for successful conversion, otherwise false
     */
    public static boolean toMapCanvasFontFormat(CanvasFont font, OutputStream stream) {
        if (font instanceof BitmapFont bitmapFont) {
            return RawBitmapFontSerializer.write(bitmapFont, stream);
        }
        return false;
    }
}
