package eu.pb4.mapcanvas.impl.font;

import com.google.gson.JsonParser;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class VanillaFontReader {
    public static BitmapFont build(ZipFile[] files, Identifier identifier) {
        var font = new BitmapFont(BitmapFont.Glyph.INVALID);

        try {
            var file = new StackedZipFile(files);

            var entry = file.getEntry("assets/" + identifier.getNamespace() + "/font/" + identifier.getPath() + ".json");
            if (entry != null) {
                var json = JsonParser.parseString(new String(file.getInputStream(entry).readAllBytes())).getAsJsonObject().getAsJsonArray("providers");

                for (var tmp : json) {
                    var obj = tmp.getAsJsonObject();

                    try {
                        if (obj.getAsJsonPrimitive("type").getAsString().equals("bitmap")) {
                            var path = new Identifier(obj.getAsJsonPrimitive("file").getAsString());
                            var ascent = obj.getAsJsonPrimitive("ascent").getAsInt();
                            var height = 8;
                            try {
                                height = obj.getAsJsonPrimitive("height").getAsInt();
                            } catch (Exception e) {
                                // NoOp
                            }

                            var texture = ImageIO.read(file.getInputStream(file.getEntry("assets/" + path.getNamespace() + "/textures/" + path.getPath())));

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

                                        font.characters.put(array[x], new BitmapFont.Glyph(charWidth, charHeight, ascent, realWidth, height, glyphTexture));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return font;
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
                } catch (Exception e) {}
            }
            return null;
        }
    }
}
