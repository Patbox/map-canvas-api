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
import eu.pb4.polymer.api.utils.PolymerUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipFile;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestMod implements ModInitializer {

    private static final Random RANDOM = new Random();

    private final PlayerCanvas canvas = DrawableCanvas.create(4, 3);
    private final DrawableCanvas drawableSurface = new CanvasImage(4 * CanvasUtils.MAP_DATA_SIZE, 3 * CanvasUtils.MAP_DATA_SIZE);
    private CanvasIcon[] icons;
    private VirtualDisplay display = null;
    private CanvasImage tater;
    private CanvasIcon fpsIcon;
    private CanvasFont font;
    private CanvasFont fontHd;
    private int msPerFrame = 1000 / 10;
    private CanvasFont fontUnsanded;
    private CanvasImage logo;
    private CanvasImage modrinth;

    private int test(CommandContext<ServerCommandSource> ctx) {
        try {
            var dir = IntegerArgumentType.getInteger(ctx, "dir");
            var rot = IntegerArgumentType.getInteger(ctx, "rot");
            if (display != null) {
                this.display.removePlayer(ctx.getSource().getPlayer());
            }
            System.out.println(dir);

            VirtualDisplay.InteractionCallback callback = (player, x, y) -> {
                var stack = player.getMainHandStack();
                if (stack.isEmpty()) {
                    return;
                }

                var count = stack.getCount();
                if (stack.getItem() instanceof DyeItem dyeItem) {
                    CanvasUtils.fill(this.drawableSurface, x - count + 1, y - count + 1, x + count, y + count, CanvasColor.from(dyeItem.getColor().getMapColor(), MapColor.Brightness.HIGH));
                } else if (stack.getItem() == Items.SPONGE) {
                    CanvasUtils.fill(this.drawableSurface, x - count + 1, y - count + 1, x + count, y + count, CanvasColor.CLEAR);
                }
            };

            this.display = VirtualDisplay.of(this.canvas, new BlockPos(ctx.getSource().getPosition()), Direction.byId(dir), rot, true, callback);
            this.display.addPlayer(ctx.getSource().getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
            this.modrinth = CanvasImage.from(ImageIO.read(new URL("https://pbs.twimg.com/media/FMZvRSUWUAE2JAd?format=png")));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            this.logo = CanvasImage.from(ImageIO.read(Files.newInputStream(FabricLoader.getInstance().getModContainer("map-canvas-api").get().getPath("assets/icon.png"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        var list = new ArrayList<CanvasIcon>();

        this.fpsIcon = this.canvas.createIcon(MapIcon.Type.TARGET_POINT, 32, 32);

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


        https://github.com/Compliance-Resource-Pack/Compliance-Java-32x/releases/download/beta-20/Compliance-32x-Java-Beta-20.zip

        for (int i = 0; i < 256; i++) {
            Text text = i % 32 == 0 ? new LiteralText("" + i) : null;
            list.add(this.canvas.createIcon(MapIcon.Type.values()[i % MapIcon.Type.values().length], i * 3, this.canvas.getHeight(), (byte) i, text));
        }

        this.icons = list.toArray(new CanvasIcon[0]);

        this.startUpdateThread();
    }

    public void update(long time, int displayFps, int frame) {
        var canvas = new CanvasImage(this.canvas.getWidth(), this.canvas.getHeight());

        /*var color = switch ((int) (time / 1000 % 4)) {
            case 0 -> CanvasColor.DIAMOND_BLUE_LOWEST;
            case 1 -> CanvasColor.DIAMOND_BLUE_LOW;
            case 2 -> CanvasColor.DIAMOND_BLUE_NORMAL;
            case 3 -> CanvasColor.DIAMOND_BLUE_HIGH;
            default -> CanvasColor.RED_HIGH;
        }*/;
        CanvasUtils.clear(canvas, CanvasColor.BLACK_LOWEST);

        CanvasUtils.fill(canvas, 32, 32, this.canvas.getWidth() - 32, this.canvas.getHeight() - 32, CanvasColor.CLEAR_FORCE);

        for (int i = 0; i < 256; i++) {
            var icon = this.icons[i];

            double input = time / 300d + i / 10d;
            var sin = Math.sin(input);
            var deltaSin = Math.sin(input + 10) - sin;

            icon.move(icon.getX(), (int) (this.canvas.getHeight() + 40 * sin), (byte) (4 + 1 * -deltaSin));
        }

        if (this.tater != null) {
            var sin = Math.sin((double) time / 300);
            var cos = Math.sin((double) time / 500 + 100000);

            CanvasUtils.draw(canvas, (int) (this.canvas.getWidth() / 2 + this.tater.getWidth() * sin), (int) (this.tater.getHeight() / 2 + 50 * cos), this.tater);
            CanvasUtils.draw(canvas, (int) (this.canvas.getWidth() / 2 + this.tater.getWidth() * cos), (int) (this.tater.getHeight() / 2 + 50 * sin), 64, 64, this.tater);
        }

        this.fpsIcon.move((this.fpsIcon.getX() + 2) % (canvas.getWidth() * 2), (this.fpsIcon.getY() + 2) % (canvas.getHeight() * 2), (byte) 0);

        CanvasUtils.draw(canvas, 0, 0, this.drawableSurface);
        DefaultFonts.VANILLA.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 65, 33, 8, CanvasColor.BLACK_NORMAL);
        DefaultFonts.VANILLA.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 32, 8, CanvasColor.RED_HIGH);
        DefaultFonts.VANILLA.drawText(canvas, "\uD83D\uDDE1\uD83C\uDFF9\uD83E\uDE93\uD83D\uDD31\uD83C\uDFA3\uD83E\uDDEA⚗ 大\t", 256, 32, 8, CanvasColor.RED_HIGH);
        DefaultFonts.VANILLA.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 64, 16, CanvasColor.RED_HIGH);

        this.fontHd.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 128, 8, CanvasColor.ORANGE_HIGH);

        this.fontHd.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 128 + 64, 16, CanvasColor.ORANGE_HIGH);
        this.fontUnsanded.drawText(canvas, "Tater best [:)  ]", 64, 128 * 2 + 32, 24, CanvasColor.BLACK_NORMAL);

        if (this.logo != null) {
            CanvasUtils.draw(canvas, canvas.getWidth() - 64, canvas.getHeight() - 64, 64, 64, this.logo);
        }

        CanvasUtils.draw(this.canvas, 0, 0, canvas);
        this.canvas.sendUpdates();
    }

    private void startUpdateThread() {
        Thread thread = new Thread("New Thread") {
            public void run() {
                try {
                    int displayFps = 0;
                    int fps = 0;
                    var lastTime = System.currentTimeMillis() / 1000;

                    while (true) {
                        var time = System.currentTimeMillis();
                        TestMod.this.update(time, displayFps, fps);
                        if (lastTime != time / 1000) {
                            displayFps = fps;
                            fps = 0;
                            lastTime = time / 1000;
                            fpsIcon.setText(new LiteralText("" + displayFps));
                        }
                        fps++;


                        Thread.sleep(Math.max(msPerFrame - (System.currentTimeMillis() - time), 2));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }

}
