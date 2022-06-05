package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.*;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.ViewUtils;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class TaterDemoRenderer implements ActiveRenderer {
    public CanvasIcon[] icons;
    public CanvasIcon fpsIcon;
    public final CanvasImage tater;
    public final CanvasFont fontHd;
    public final CanvasImage logo;
    private DrawableCanvas drawableSurface;


    public TaterDemoRenderer(CanvasImage tater, CanvasFont fontHd, CanvasImage logo) {
        this.tater = tater;
        this.fontHd = fontHd;
        this.logo = logo;
    }

    @Override
    public void setup(PlayerCanvas canvas) {
        this.fpsIcon = canvas.createIcon(MapIcon.Type.TARGET_POINT, 32, 32);

        var list = new ArrayList<CanvasIcon>();
        for (int i = 0; i < 256; i++) {
            Text text = i % 32 == 0 ? Text.literal("" + i) : null;
            list.add(canvas.createIcon(MapIcon.Type.values()[i % MapIcon.Type.values().length], i * 3, canvas.getHeight(), (byte) i, text));
        }

        this.icons = list.toArray(new CanvasIcon[0]);

        this.drawableSurface = new CanvasImage(canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        CanvasUtils.clear(canvas, CanvasColor.BLACK_LOWEST);

        CanvasUtils.fill(canvas, 32, 32, outputCanvas.getWidth() - 32, outputCanvas.getHeight() - 32, CanvasColor.CLEAR_FORCE);

        this.fpsIcon.setText(Text.literal("" + displayFps));

        for (int i = 0; i < 256; i++) {
            var icon = this.icons[i];

            double input = time / 300d + i / 10d;
            var sin = Math.sin(input);
            var deltaSin = Math.sin(input + 10) - sin;

            icon.move(icon.getX(), (int) (outputCanvas.getHeight() + 40 * sin), (byte) (4 + 1 * -deltaSin));
        }

        if (this.tater != null) {
            var sin = Math.sin((double) time / 300);
            var cos = Math.sin((double) time / 500 + 100000);

            var rotater = ViewUtils.rotate(this.tater, (float) MathHelper.wrapDegrees((double) time / 2000));

            CanvasUtils.draw(canvas, canvas.getWidth() / 2 - rotater.getWidth() / 2, canvas.getHeight() / 2 - rotater.getHeight() / 2, rotater);
            CanvasUtils.draw(canvas, (int) (outputCanvas.getWidth() / 2 + this.tater.getWidth() * cos), (int) (this.tater.getHeight() / 2 + 50 * sin), 64, 64, this.tater);
        }

        this.fpsIcon.move((this.fpsIcon.getX() + 2) % (canvas.getWidth() * 2), (this.fpsIcon.getY() + 2) % (canvas.getHeight() * 2), (byte) 0);

        CanvasUtils.draw(canvas, 0, 0, this.drawableSurface);
        DefaultFonts.VANILLA.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 65, 33, 8, CanvasColor.BLACK_NORMAL);
        DefaultFonts.VANILLA.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 32, 8, CanvasColor.RED_HIGH);
        DefaultFonts.VANILLA.drawText(canvas, "\uD83D\uDDE1\uD83C\uDFF9\uD83E\uDE93\uD83D\uDD31\uD83C\uDFA3\uD83E\uDDEA⚗ 大\t", 256, 32, 8, CanvasColor.RED_HIGH);
        DefaultFonts.VANILLA.drawText(ViewUtils.flipX(canvas), "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 64, 16, CanvasColor.RED_HIGH);

        this.fontHd.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 128, 8, CanvasColor.ORANGE_HIGH);

        this.fontHd.drawText(canvas, "Hello World! 1234 \n[ą]ęść AĄĘŚĆ \u00a1", 64, 128 + 64, 16, CanvasColor.ORANGE_HIGH);
        DefaultFonts.UNSANDED.drawText(canvas, "Tater best [:)  ]", 64, 128 * 2 + 32, 24, CanvasColor.BLACK_NORMAL);
        DefaultFonts.UNSANDED.drawText(canvas, "T\u200cater best [:)  ]", 64, 128 * 2 + 32 + 25, 24, CanvasColor.BLACK_NORMAL);

        if (this.logo != null) {
            CanvasUtils.draw(canvas, canvas.getWidth() - 64, canvas.getHeight() - 64, 64, 64, this.logo);
        }
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
        if (type == ClickType.RIGHT) {
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
        }
    }
}
