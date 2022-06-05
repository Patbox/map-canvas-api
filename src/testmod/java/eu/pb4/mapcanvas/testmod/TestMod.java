package eu.pb4.mapcanvas.testmod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import eu.pb4.polymer.api.utils.PolymerUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
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


    private int test(CommandContext<ServerCommandSource> ctx) {
        try {
            var dir = IntegerArgumentType.getInteger(ctx, "dir");
            var rot = IntegerArgumentType.getInteger(ctx, "rot");
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

            this.display = VirtualDisplay.of(this.canvas, new BlockPos(ctx.getSource().getPosition()), Direction.byId(dir), rot, true, callback);
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
                                    .then(argument("rot", IntegerArgumentType.integer(0, 4))
                                            .executes(this::test)
                                    )
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
            var vanillaZip = PolymerUtils.getClientJar();

            var compPath = FabricLoader.getInstance().getGameDir().resolve("coml.zip");
            var unsPath = FabricLoader.getInstance().getGameDir().resolve("uns.zip");
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

            var hdPack = new ZipFile(compPath.toFile());
            var unsPack = new ZipFile(unsPath.toFile());
            var jsonPack = new ZipFile(vanillaJsonPath.toFile());

            this.font = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"), vanillaZip);
            this.fontHd = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"), hdPack, vanillaZip);
            this.fontUnsanded = FontUtils.fromVanillaFormat(new Identifier("minecraft:default"), unsPack, jsonPack);

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
                var stream = new FileOutputStream(path.resolve("alt.mcaf").toFile());

                RawBitmapFontSerializer.write((BitmapFont) FontUtils.fromVanillaFormat(new Identifier("minecraft:alt"), vanillaZip), stream);
                stream.close();
            }

            {
                var stream = new FileOutputStream(path.resolve("illageralt.mcaf").toFile());
                RawBitmapFontSerializer.write((BitmapFont) FontUtils.fromVanillaFormat(new Identifier("minecraft:illageralt"), vanillaZip), stream);
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


        ServerLifecycleEvents.SERVER_STARTED.register((s) -> {
            this.setCurrentRenderer(this.menuRenderer);
            this.renderers.add(new Pair<>("TaterDemo", new TaterDemoRenderer(this.tater, this.fontHd, this.logo)));
            this.renderers.add(new Pair<>("SimpleSurface", new SurfaceRenderer()));
            this.renderers.add(new Pair<>("Raycast", new RaycastRenderer(s)));

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
        this.currentRenderer = null;
        CanvasUtils.clear(this.canvas);
        this.canvas.clearIcons();
        renderer.setup(this.canvas);
        this.currentRenderer = renderer;
    }
}
