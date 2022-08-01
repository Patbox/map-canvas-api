package eu.pb4.mapcanvas.impl.font;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.zip.*;

public class RawBitmapFontSerializer {
    private static final short MAGIC = 0x7CAF;

    private static final byte VERSION = 1;

    public static boolean write(BitmapFont font, OutputStream stream) {
        return write(font, stream, true);
    }

    public static boolean write(BitmapFont font, OutputStream stream, boolean compress) {
        try {
            stream.write(Shorts.toByteArray(MAGIC));
            stream.write(1);

            if (compress) {
                stream.write(1);
                var cStream = new GZIPOutputStream(stream) {
                    public void setLevel( int level ) {
                        def.setLevel(level);
                    }
                };

                cStream.setLevel(Deflater.BEST_COMPRESSION);
                stream = cStream;
            } else {
                stream.write(0);
            }

            {
                var bytes = font.getMetadata().name().getBytes(StandardCharsets.UTF_8);
                writeVarInt(bytes.length, stream);
                stream.write(bytes);
            }

            writeVarInt(font.getMetadata().authors().size(), stream);

            for (var author : font.getMetadata().authors()) {
                var bytes = author.getBytes(StandardCharsets.UTF_8);

                writeVarInt(bytes.length, stream);
                stream.write(bytes);
            }

            if (font.getMetadata().description().isPresent()) {
                var bytes = font.getMetadata().description().get().getBytes(StandardCharsets.UTF_8);
                writeVarInt(bytes.length, stream);
                stream.write(bytes);
            } else {
                writeVarInt(0, stream);
            }


            writeVarInt(font.characters.size(), stream);

            for (var entry : font.characters.int2ObjectEntrySet()) {
                writeVarInt(entry.getIntKey(), stream);
                writeGlyph(entry.getValue(), stream);
            }

            writeGlyph(font.defaultGlyph, stream);

            if (stream instanceof GZIPOutputStream gzipOutputStream) {
                gzipOutputStream.finish();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static BitmapFont read(InputStream stream) {
        try {
            if (Shorts.fromByteArray(stream.readNBytes(2)) == MAGIC) {
                var version = stream.read();

                CanvasFont.Metadata metadata;
                if (version == 0) {
                    metadata = CanvasFont.Metadata.empty();
                } else if (version == 1) {
                    if (stream.read() == 1) {
                        stream = new GZIPInputStream(stream);
                    }

                    String name = new String(stream.readNBytes(readVarInt(stream)), StandardCharsets.UTF_8);

                    var size = readVarInt(stream);
                    var authors = new ArrayList<String>(size);

                    for (int i = 0; i < size; i++) {
                        authors.add(new String(stream.readNBytes(readVarInt(stream)), StandardCharsets.UTF_8));
                    }

                    var descSize = readVarInt(stream);
                    String description;
                    if (descSize > 0) {
                        description = new String(stream.readNBytes(descSize), StandardCharsets.UTF_8);
                    } else {
                        description = null;
                    }

                    metadata = CanvasFont.Metadata.create(name, authors, description);
                } else {
                    throw new RuntimeException("Unsupported font version");
                }

                var map = new Int2ObjectOpenHashMap<BitmapFont.Glyph>();

                int size = readVarInt(stream);

                for (int x = 0; x < size; x++) {
                    map.put(readVarInt(stream), readGlyph(stream));
                }

                var defaultGlyph = readGlyph(stream);

                return new BitmapFont(defaultGlyph, map, metadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeGlyph(BitmapFont.Glyph glyph, OutputStream stream) throws IOException {
        writeVarInt(glyph.width(), stream);
        writeVarInt(glyph.height(), stream);
        writeVarInt(glyph.ascend(), stream);
        writeVarInt(glyph.fontWidth(), stream);
        writeVarInt(glyph.logicalHeight(), stream);

        var set = new BitSet(glyph.texture().length);
        for (int i = 0; i < glyph.texture().length; i++) {
            set.set(i, glyph.texture()[i]);
        }
        var bytes = set.toByteArray();
        writeVarInt(bytes.length, stream);
        stream.write(bytes);
    }

    private static BitmapFont.Glyph readGlyph(InputStream stream) throws IOException {
        var width = readVarInt(stream);
        var height = readVarInt(stream);
        var ascend = readVarInt(stream);
        var fontWidth = readVarInt(stream);
        var logicalHeight = readVarInt(stream);

        var length = readVarInt(stream);
        var texture = new boolean[height * width];
        var set = BitSet.valueOf(stream.readNBytes(length));
        for (int i = 0; i < set.length(); i++) {
            texture[i] = set.get(i);
        }

        return new BitmapFont.Glyph(width, height, ascend, fontWidth, logicalHeight, texture);
    }

    private static void writeVarInt(int value, OutputStream stream) throws IOException {
        while((value & -128) != 0) {
            stream.write(value & 127 | 128);
            value >>>= 7;
        }

        stream.write(value);
    }

    public static int readVarInt(InputStream stream) throws IOException {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = (byte) stream.read();
            i |= (b & 127) << j++ * 7;
            if (j > 5) {
                return Integer.MAX_VALUE;
            }
        } while((b & 128) == 128);

        return i;
    }
}
