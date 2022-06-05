package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.BitmapFont;
import eu.pb4.mapcanvas.impl.font.RawBitmapFontSerializer;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Default, bundled fonts
 */
public final class DefaultFonts {
    private DefaultFonts() {}
    /**
     * Default, vanilla Minecraft font
     */
    public static final CanvasFont VANILLA;

    /**
     * Standard Galactic Alphabet (the enchanting text) font
     */
    public static final CanvasFont ALT;

    /**
     * Rune like font of Illagers, from MC dungeons
     */
    public static final CanvasFont ILLAGER_ALT;

    /**
     * Unsanded by unascribed
     * https://www.curseforge.com/minecraft/texture-packs/unsanded
     */
    public static final CanvasFont UNSANDED;

    static {
        var path = FabricLoader.getInstance().getModContainer("map-canvas-api").get().getPath("fonts");
        VANILLA = read(path.resolve("vanilla.mcaf"));
        ALT = read(path.resolve("alt.mcaf"));
        ILLAGER_ALT = read(path.resolve("illageralt.mcaf"));
        UNSANDED = FontUtils.merge(read(path.resolve("unsanded.mcaf")), VANILLA);
    }

    private static CanvasFont read(Path path) {
        CanvasFont font;
        try {
            font = RawBitmapFontSerializer.read(Files.newInputStream(path));
        } catch (Exception e) {
            font = BitmapFont.EMPTY;
            e.printStackTrace();
        }
        return font != null ? font : BitmapFont.EMPTY;
    }
}
