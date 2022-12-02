package eu.pb4.mapcanvas.testmod;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.mapcanvas.api.core.*;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.font.FontUtils;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.mapcanvas.impl.font.BitmapFont;
import eu.pb4.mapcanvas.impl.font.RawBitmapFontSerializer;
import eu.pb4.mapcanvas.testmod.advancedgui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.MapColor;
import net.minecraft.command.argument.BlockRotationArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipFile;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestMod implements ModInitializer {

    private static final Random RANDOM = new Random();

    private final PlayerCanvas canvas = DrawableCanvas.create(6, 4);

    private VirtualDisplay display = null;

    private int msPerFrame = 1000 / 10;
    private CanvasFont fontUnsanded;
    private CanvasImage lastImage = null;
    private CanvasImage logo;
    private CanvasFont font;
    private CanvasFont fontHd;
    private CanvasImage tater;
    private volatile ActiveRenderer currentRenderer;
    private List<Pair<String, ActiveRenderer>> renderers = new ArrayList<>();
    private final MenuRenderer menuRenderer = new MenuRenderer(this::setCurrentRenderer, renderers);
    private List<Runnable> runPreRender = new ArrayList<>();
    private Thread rendererThread;
    private volatile boolean activeRenderer = false;
    private CanvasFont pixel;


    private int test(CommandContext<ServerCommandSource> ctx) {
        try {
            var dir = IntegerArgumentType.getInteger(ctx, "dir");
            var rot = BlockRotationArgumentType.getBlockRotation(ctx, "rot");
            if (display != null) {
                this.display.destroy();
            }
            System.out.println(dir);

            VirtualDisplay.TypedInteractionCallback callback = (player, type, x, y) -> {
                this.runPreRender.add(() -> {
                    if (x >= canvas.getWidth() - 32 && y < 32) {
                        this.setCurrentRenderer(this.menuRenderer);
                    } else {
                        this.currentRenderer.onClick(player, type, x, y);
                    }
                });
            };

            this.display = VirtualDisplay.builder(this.canvas, new BlockPos(ctx.getSource().getPosition()), Direction.byId(dir))
                    .rotation(rot).invisible().glowing().callback(callback).build();
            for (var player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                this.display.addPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("test").then(
                            argument("dir", IntegerArgumentType.integer(0, Direction.values().length))
                                    .then(argument("rot", BlockRotationArgumentType.blockRotation())
                                            .executes(this::test)
                                    )
                    )
            );
            dispatcher.register(
                    literal("input").then(
                            argument("input", StringArgumentType.greedyString())
                                    .executes((ctx) -> {
                                        this.currentRenderer.onInput(StringArgumentType.getString(ctx, "input"));
                                        return 0;
                                    })

                    )
            );

            dispatcher.register(literal("setfps").then(
                    argument("fps", IntegerArgumentType.integer(1))
                            .executes((ctx) -> {
                                this.msPerFrame = 1000 / IntegerArgumentType.getInteger(ctx, "fps");
                                return 0;
                            }))
            );

            dispatcher.register(
                    literal("saveimage").executes((ctx -> {
                        var image = CanvasUtils.toImage(this.lastImage);

                        try {
                            ImageIO.write(image, "png", Files.newOutputStream(FabricLoader.getInstance().getGameDir().resolve("output.png"), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }))
            );
        });

        ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> {
            this.canvas.addPlayer(handler);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((ServerPlayNetworkHandler handler, MinecraftServer server) -> {
            this.canvas.removePlayer(handler);
        });

        try {
            var image = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/552976908070027270/854881217950122024/tinypotato.png"));

            for (int i = 0; i < 10; i++){
                long start = System.currentTimeMillis();
                this.tater = CanvasImage.from(image);
                System.out.println("Buildin time passed = " + (System.currentTimeMillis() - start));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            this.logo = CanvasImage.from(ImageIO.read(Files.newInputStream(FabricLoader.getInstance().getModContainer("map-canvas-api").get().getPath("assets/icon.png"))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            var json = new JsonObject();

            for (var color : CanvasColor.values()) {
                var obj = new JsonObject();

                obj.addProperty("rawId", Byte.toUnsignedInt(color.getRenderColor()));
                obj.addProperty("mapColor", color.getColor().id);
                obj.addProperty("mapBrightness", color.getBrightness().id);
                obj.addProperty("colorInt", color.getRgbColor());
                obj.addProperty("clear", color.getColor() == MapColor.CLEAR);

                var sb = new StringBuilder(Integer.toHexString(color.getRgbColor()));
                while (sb.length() < 6) {
                    sb.insert(0, '0'); // pad with leading zero if needed
                }

                obj.addProperty("color", sb.toString());
                json.add(color.getName(), obj);
            }

            Files.writeString(FabricLoader.getInstance().getGameDir().resolve("../palette/map_colors.json"), json.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            var builder = new StringBuilder();
            builder.append("GIMP Palette\n");
            builder.append("Name: Minecraft Map Colors\n");
            builder.append("Columns: 16\n");
            builder.append("#\n");


            for (var color : CanvasColor.values()) {
                var rgb = color.getRgbColor();
                builder.append("" + ((rgb >> 16) & 0xFF)).append(" ");
                builder.append("" + ((rgb >> 8) & 0xFF)).append(" ");
                builder.append("" + ((rgb) & 0xFF)).append(" ");
                builder.append(color.getName()).append("\n");
            }

            Files.writeString(FabricLoader.getInstance().getGameDir().resolve("../palette/map_colors_gimp.gpl"), builder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            var vanillaZip = new ZipFile(FabricLoader.getInstance().getGameDir().resolve("polymer_cache/client_jars/7e46fb47609401970e2818989fa584fd467cd036.jar").toFile());//PolymerUtils.getClientJar();

            var compPath = FabricLoader.getInstance().getGameDir().resolve("coml.zip");
            var unsPath = FabricLoader.getInstance().getGameDir().resolve("uns.zip");
            var pixelPath = FabricLoader.getInstance().getGameDir().resolve("pixelperf.zip");
            var vanillaJsonPath = FabricLoader.getInstance().getGameDir().resolve("../vanilla-font-json.zip");

            if (!Files.exists(compPath)) {
                FileUtils.copyURLToFile(
                        new URL("https://github.com/Compliance-Resource-Pack/Compliance-Java-32x/releases/download/beta-20/Compliance-32x-Java-Beta-20.zip"),
                        compPath.toFile(),
                        10000,
                        10000);
            }

            if (!Files.exists(unsPath)) {
                FileUtils.copyURLToFile(
                        new URL("https://media.forgecdn.net/files/3354/621/Unsanded-1.1.zip"),
                        unsPath.toFile(),
                        10000,
                        10000);
            }

            if (!Files.exists(pixelPath)) {
                FileUtils.copyURLToFile(
                        new URL("https://github.com/NovaWostra/Pixel-Perfection-Chorus-Eddit/releases/download/v9.19.6.1/Pixel.Perfection.Legacy.9.19.6.1.zip"),
                        pixelPath.toFile(),
                        10000,
                        10000);
            }


            var hdPack = new ZipFile(compPath.toFile());
            var unsPack = new ZipFile(unsPath.toFile());
            var pixelPack = new ZipFile(pixelPath.toFile());
            var jsonPack = new ZipFile(vanillaJsonPath.toFile());

            this.font = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"),
                    CanvasFont.Metadata.create("Minecraft Font", List.of("Mojang Studios"), "Default Minecraft Font"),
                    vanillaZip);
            this.fontHd = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"), hdPack, vanillaZip);
            this.pixel = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"), CanvasFont.Metadata.create("Pixel Perfection", List.of("XSSheep"), "Font from Pixel Perfection Resource Pack"), pixelPack, jsonPack);
            this.fontUnsanded = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"),
                    CanvasFont.Metadata.create("Unsanded", List.of("unascribed"), "An 8px font with wide vertical strokes inspired by Chicago and Craft Sans."),
                    unsPack, jsonPack);

            var path = FabricLoader.getInstance().getGameDir().resolve("fonts_export");
            Files.createDirectories(path);

            {
                var stream = new FileOutputStream(path.resolve("vanilla.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) this.font, stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("unsanded.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) this.fontUnsanded, stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("pixel_perfection.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) this.pixel, stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("alt.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) FontUtils.fromVanillaFormat(new Identifier("minecraft:alt"),
                        CanvasFont.Metadata.create("Minecraft Alt", List.of("Mojang Studios"), "Standard Galactic Alphabet font"),
                        vanillaZip), stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("illageralt.mcaf").toFile());
                RawBitmapFontSerializer.write((BitmapFont) FontUtils.fromVanillaFormat(new Identifier("minecraft:illageralt"),
                        CanvasFont.Metadata.create("Illager Runes", List.of("Mojang Studios"), "Illager Runes from Minecraft Dungeons"),
                        vanillaZip), stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("faithful.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) fontHd, stream);
                stream.close();
            }

            vanillaZip.close();
            hdPack.close();
            unsPack.close();
            jsonPack.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        FontTestRenderer.FONTS.add(this.fontHd);
        FontTestRenderer.FONTS.add(this.pixel);
        FontTestRenderer.FONTS.add(FontUtils.fromAwtFont(new Font("Comic Sans MS", Font.PLAIN, 64)));

        ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
            this.setCurrentRenderer(this.menuRenderer);
            this.renderers.add(new Pair<>("TaterDemo", new TaterDemoRenderer(this.tater, this.fontHd, this.logo)));
            this.renderers.add(new Pair<>("SimpleSurface", new SurfaceRenderer()));
            this.renderers.add(new Pair<>("Raycast", new RaycastRenderer(s)));
            this.renderers.add(new Pair<>("FontTest", new FontTestRenderer()));
            try {
                this.renderers.add(new Pair<>("Browser Test", new BrowserTestRenderer()));
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                this.renderers.add(new Pair<>("Client Framebuffer", new ClientRenderer()));
            }

            this.startUpdateThread();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register((s) -> {
            this.setCurrentRenderer(this.menuRenderer);
            this.renderers.clear();
            this.activeRenderer = false;
        });

    }

    public void update(long time, int displayFps, int frame) throws InterruptedException {
        if (this.currentRenderer == null) {
            return;
        }

        try {
            var canvas = new CanvasImage(this.canvas.getWidth(), this.canvas.getHeight());
            this.currentRenderer.render(this.canvas, canvas, time, displayFps, frame);

            if (this.currentRenderer != this.menuRenderer) {
                CanvasUtils.fill(canvas, canvas.getWidth() - 32, 0, canvas.getWidth(), 32, CanvasColor.RED_LOW);
                DefaultFonts.UNSANDED.drawGlyph(canvas, 'X', canvas.getWidth() - 28, 4, 24, 0, CanvasColor.BLACK_HIGH);
            }
            CanvasUtils.draw(this.canvas, 0, 0, canvas);
            this.lastImage = canvas;
            this.canvas.sendUpdates();
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(1000);
        }
    }

    private void startUpdateThread() {
        this.activeRenderer = true;
        this.rendererThread = new Thread("New Thread") {
            public void run() {
                try {
                    int displayFps = 0;
                    int fps = 0;
                    var lastTime = System.currentTimeMillis() / 1000;

                    while (TestMod.this.activeRenderer) {
                        var time = System.currentTimeMillis();
                        var preRender = TestMod.this.runPreRender;
                        if (!preRender.isEmpty()) {
                            TestMod.this.runPreRender = new ArrayList<>();
                            for (var r : preRender) {
                                r.run();
                            }
                        }

                        TestMod.this.update(time, displayFps, fps);
                        if (lastTime != time / 1000) {
                            displayFps = fps;
                            fps = 0;
                            lastTime = time / 1000;
                        }
                        fps++;


                        Thread.sleep(Math.max(msPerFrame - (System.currentTimeMillis() - time), 2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        this.rendererThread.setDaemon(true);
        this.rendererThread.start();
    }


    protected void setCurrentRenderer(ActiveRenderer renderer) {
        try {
            if (this.currentRenderer != null) {
                this.currentRenderer.setStatus(false);
            }

            this.currentRenderer = null;
            CanvasUtils.clear(this.canvas);
            this.canvas.clearIcons();
            renderer.setup(this.canvas);
            this.currentRenderer = renderer;
            this.currentRenderer.setStatus(true);
        } catch (Throwable e) {
            this.setCurrentRenderer(this.menuRenderer);
        }
    }

    {
        System.out.println(java.awt.GraphicsEnvironment.isHeadless());
    }
}
