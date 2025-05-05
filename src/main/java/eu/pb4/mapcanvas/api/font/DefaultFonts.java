package eu.pb4.mapcanvas.api.font;

import eu.pb4.mapcanvas.impl.font.BitmapFont;
import eu.pb4.mapcanvas.impl.font.LazyFont;
import eu.pb4.mapcanvas.impl.font.MojangUnifontFont;
import eu.pb4.mapcanvas.impl.font.StackedLazyFont;
import eu.pb4.mapcanvas.impl.font.serialization.RawBitmapFontSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
     * <a href="https://modrinth.com/resourcepack/unsanded">...</a>
     */
    public static final CanvasFont UNSANDED;


    /**
     * Unsanded by unascribed
     * This version doesn't contain vanilla-filled characters
     * <a href="https://modrinth.com/resourcepack/unsanded">...</a>
     */
    public static final CanvasFont UNSANDED_BASE;

    /**
     * Unifont (Minecraft's fallback font)
     * <a href="https://unifoundry.com/unifont/index.html">...</a>
     */
    public static final LazyCanvasFont UNIFONT;

    /**
     * Unifont (Minecraft's fallback font), Japanese variant
     * <a href="https://unifoundry.com/unifont/index.html">...</a>
     */
    public static final LazyCanvasFont UNIFONT_JP;


    static {
        var path = FabricLoader.getInstance().getModContainer("map-canvas-api").get().getPath("fonts");
        VANILLA = REGISTRY.register(Identifier.of("minecraft:default"), read(path.resolve("vanilla.mcaf")));
        ALT = REGISTRY.register(Identifier.of("minecraft:alt"), read(path.resolve("alt.mcaf")));
        ILLAGER_ALT = REGISTRY.register(Identifier.of("minecraft:illageralt"), read(path.resolve("illageralt.mcaf")));
        UNSANDED_BASE = REGISTRY.register(Identifier.of("unsanded:base"), read(path.resolve("unsanded.mcaf")));
        UNSANDED = REGISTRY.register(Identifier.of("unsanded:full"), FontUtils.merge(UNSANDED_BASE, VANILLA));
        UNIFONT = REGISTRY.register(Identifier.of("unifont:default"), new MojangUnifontFont(CanvasFont.Metadata.create("Unifont",
                List.of("Roman Czyborra", "Paul Hardy"),
                "https://scripts.sil.org/OFL"),
                "unifont_all_no_pua-15.1.05", "https://resources.download.minecraft.net/66/666999116c467f10622db1527a06ddc0a6efad2a"));
        var jp = CanvasFont.Metadata.create("Unifont JP",
                List.of("Roman Czyborra", "Paul Hardy"),
                "https://scripts.sil.org/OFL");
        UNIFONT_JP = REGISTRY.register(Identifier.of("unifont:japanese"), new StackedLazyFont(new LazyCanvasFont[]{
                new MojangUnifontFont(jp, "unifont_jp_patch-15.1.05",
                        "https://resources.download.minecraft.net/66/666999116c467f10622db1527a06ddc0a6efad2a"),
                UNIFONT}, jp));
        REGISTRY.register(Identifier.of("minecraft:uniform"), UNIFONT);
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
