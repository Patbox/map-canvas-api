package eu.pb4.mapcanvas.impl.font;

import com.google.gson.JsonParser;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class VanillaFontReader {

    public static CanvasFont build(ZipFile[] zipFile, Identifier identifier) {
        var lines = new ArrayList<String>();
        for (var zip : zipFile) {
            try {
                var entry = zip.getEntry("pack.mcmeta");
                var stream = zip.getInputStream(entry);
                var json = JsonParser.parseString(new String(stream.readAllBytes()));
                stream.close();

                lines.add(Text.Serialization.fromJsonTree(json.getAsJsonObject().get("pack").getAsJsonObject().get("description")).getString());
            } catch (Exception e) {

            }
        }

        return build(zipFile,
                CanvasFont.Metadata.create("Resource Pack Font", List.of("Unknown"), "Generated from resource packs.\n" + String.join("\n", lines))
                , identifier);
    }


    public static BitmapFont build(ZipFile[] files, CanvasFont.Metadata metadata, Identifier identifier) {
        var font = new BitmapFont(BitmapFont.Glyph.INVALID, metadata);

        try {
            var file = new StackedZipFile(files);

            parseFontFile(file, font, identifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return font;
    }

    private static void parseFontFile(StackedZipFile file, BitmapFont font, Identifier identifier) {
        try {
            var entry = file.getEntry("assets/" + identifier.getNamespace() + "/font/" + identifier.getPath() + ".json");
            if (entry != null) {
                var json = JsonParser.parseString(new String(file.getInputStream(entry).readAllBytes())).getAsJsonObject().getAsJsonArray("providers");

                for (var tmp : json) {
                    var obj = tmp.getAsJsonObject();

                    try {
                        var type = obj.getAsJsonPrimitive("type").getAsString();
                        switch (type) {
                            case "bitmap" -> {
                                var path = new Identifier(obj.getAsJsonPrimitive("file").getAsString());
                                var ascent = obj.getAsJsonPrimitive("ascent").getAsInt();
                                var height = 8;
                                try {
                                    height = obj.getAsJsonPrimitive("height").getAsInt();
                                } catch (Exception e) {
                                    // NoOp
                                }

                                var input = file.getInputStream(file.getEntry("assets/" + path.getNamespace() + "/textures/" + path.getPath()));
                                if (input == null) {
                                    continue;
                                }
                                var texture = ImageIO.read(input);

                                var charJson = obj.getAsJsonArray("chars");
                                var charWidth = texture.getWidth() / charJson.get(0).getAsString().length();
                                var charHeight = texture.getHeight() / charJson.size();

                                for (int y = 0; y < charJson.size(); y++) {
                                    var chars = charJson.get(y).getAsString();
                                    var array = chars.codePoints().toArray();

                                    for (int x = 0; x < array.length; x++) {
                                        try {
                                            var glyphTexture = new boolean[charHeight * charWidth];
                                            int realWidth = 0;
                                            for (int xd = 0; xd < charWidth; xd++) {
                                                for (int yd = 0; yd < charHeight; yd++) {
                                                    if ((texture.getRGB(x * charWidth + xd, y * charHeight + yd) >> 24 & 0xFF) > 64) {
                                                        glyphTexture[xd + yd * charWidth] = true;
                                                        realWidth = Math.max(realWidth, xd);
                                                    }
                                                }
                                            }

                                            if (!font.characters.containsKey(array[x])) {
                                                var trueWidth = realWidth + 1;
                                                var textureCompact = new boolean[charHeight * trueWidth];

                                                for (int xd = 0; xd < trueWidth; xd++) {
                                                    for (int yd = 0; yd < charHeight; yd++) {
                                                        textureCompact[xd + yd * trueWidth] = glyphTexture[xd + yd * charWidth];
                                                    }
                                                }
                                                font.characters.put(array[x], new BitmapFont.Glyph(trueWidth, charHeight, ascent, realWidth, height, textureCompact));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            case "space" -> {
                                var advances = obj.get("advances").getAsJsonObject();

                                for (var key : advances.keySet()) {
                                    font.characters.put(key.codePointAt(0), new BitmapFont.Glyph(0, 0, 0, advances.getAsJsonPrimitive(key).getAsInt(), 0, new boolean[0]));
                                }
                            }
                            case "reference" -> {
                                var id = Identifier.tryParse(obj.get("id").getAsString());
                                if (id != null) {
                                    parseFontFile(file, font, id);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public record StackedZipFile(ZipFile[] files) {
        public ZipEntry getEntry(String path) {
            for (var file : files) {
                var entry = file.getEntry(path);
                if (entry != null) {
                    return entry;
                }
            }
            return null;
        }

        public InputStream getInputStream(ZipEntry entry) {
            for (var file : files) {
                try {
                    var stream = file.getInputStream(entry);
                    if (stream != null) {
                        return stream;
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }
    }
}
