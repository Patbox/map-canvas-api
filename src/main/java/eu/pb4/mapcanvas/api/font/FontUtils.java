package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.BitmapFont;
import eu.pb4.mapcanvas.impl.font.RawBitmapFontSerializer;
import eu.pb4.mapcanvas.impl.font.StackedFont;
import eu.pb4.mapcanvas.impl.font.VanillaFontReader;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.OutputStream;
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
     * Reads font from Map Canvas API Font format
     * @param stream stream for files/bytes of font
     * @return New canvas font
     */
    public static CanvasFont fromMapCanvasFontFormat(InputStream stream) {
        var font = RawBitmapFontSerializer.read(stream);
        return font != null ? font : BitmapFont.EMPTY;
    }

    /**
     * Writes font to Map Canvas API Font format
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
