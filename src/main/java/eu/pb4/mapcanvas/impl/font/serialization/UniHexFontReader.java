package eu.pb4.mapcanvas.impl.font.serialization;

import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.impl.font.BitmapFont;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;

public class UniHexFontReader {
    public static BitmapFont build(Path hexFile, CanvasFont.Metadata metadata) throws IOException {
        return build(Files.newInputStream(hexFile), metadata);
    }
    public static BitmapFont build(InputStream hexFile, CanvasFont.Metadata metadata) throws IOException {
        var font = new BitmapFont(BitmapFont.Glyph.INVALID, metadata);
        parse(hexFile, font);
        return font;
    }

    private static void parse(InputStream hexFile, BitmapFont font) throws IOException {
        var height = 16;
        while (hexFile.available() > 0) {
            var num = new StringBuilder();
            var bitmap = new StringBuilder();
            var current = num;
            while (true) {
                var chr = hexFile.read();
                if (chr == -1) {
                    if (num == current) {
                        return;
                    } else {
                        break;
                    }
                } else if (chr == '\n') {
                    break;
                } else if (chr == ':') {
                    current = bitmap;
                } else {
                    current.append((char) chr);
                }
            }
            var codepoint = Integer.parseInt(num.toString(), 16);
            var bytes = HexFormat.of().parseHex(bitmap.toString());
            var width = bytes.length / 2;
            var texture = new boolean[width * height];
            var realWidth = 0;
            for (var i = 0; i < bytes.length; i++) {
                var b = Byte.toUnsignedInt(bytes[i]);
                if (b == 0) {
                    continue;
                }
                for (int a = 0; a < 8; a++) {
                    var value = ((b >> a) & 0x1) == 0x1;
                    if (value) {
                        texture[i * 8 + 7 - a] = true;
                        realWidth = Math.max(realWidth, (i * 8 + a) % width);
                    }
                }
            }
            font.characters.put(codepoint, new BitmapFont.Glyph(width, height, 7, realWidth, 8, texture));
        }
    }
}
