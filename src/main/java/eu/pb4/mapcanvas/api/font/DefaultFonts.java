package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.BitmapFont;
import eu.pb4.mapcanvas.impl.font.RawBitmapFontSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Default, bundled fonts
 */
public final class DefaultFonts {
    public static final FontRegistry REGISTRY = new FontRegistry();

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
     * <a href="https://www.curseforge.com/minecraft/texture-packs/unsanded">...</a>
     */
    public static final CanvasFont UNSANDED;


    /**
     * Unsanded by unascribed
     * This version doesn't contain vanilla-filled characters
     * <a href="https://www.curseforge.com/minecraft/texture-packs/unsanded">...</a>
     */
    public static final CanvasFont UNSANDED_BASE;

    static {
        var path = FabricLoader.getInstance().getModContainer("map-canvas-api").get().getPath("fonts");
        VANILLA = REGISTRY.register(Identifier.of("minecraft:default"), read(path.resolve("vanilla.mcaf")));
        ALT = REGISTRY.register(Identifier.of("minecraft:alt"), read(path.resolve("alt.mcaf")));
        ILLAGER_ALT = REGISTRY.register(Identifier.of("minecraft:illageralt"), read(path.resolve("illageralt.mcaf")));
        UNSANDED_BASE = REGISTRY.register(Identifier.of("unsanded:base"), read(path.resolve("unsanded.mcaf")));
        UNSANDED = REGISTRY.register(Identifier.of("unsanded:full"), FontUtils.merge(UNSANDED_BASE, VANILLA));
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
