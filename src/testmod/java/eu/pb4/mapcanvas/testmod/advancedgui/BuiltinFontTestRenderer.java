package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuiltinFontTestRenderer implements ActiveRenderer {
    private Text userText = Text.empty();
    private List<Identifier> fonts;

    @Override
    public void setup(PlayerCanvas canvas) {
        this.fonts = List.copyOf(DefaultFonts.REGISTRY.fonts());
    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        CanvasUtils.clear(canvas, CanvasColor.WHITE_HIGH);
        var id = this.fonts.get((int) (time / 1000 / 3) % this.fonts.size());
        var font = DefaultFonts.REGISTRY.getFont(id);
        assert font != null;
        DefaultFonts.VANILLA.drawText(canvas, id.toString(), 2, 2, 8, CanvasColor.LIGHT_GRAY_HIGH);

        int i = 16;

        font.drawText(canvas, font.getMetadata().name(), 16 + 2, i + 2, 32, CanvasColor.LIGHT_GRAY_HIGH);
        font.drawText(canvas, font.getMetadata().name(), 16, i, 32, CanvasColor.BLACK_HIGH);

        i += 32 + 8;
        for (var author : font.getMetadata().authors()) {
            font.drawText(canvas, author, 16, i, 16, CanvasColor.BLACK_HIGH);
            i += 16 + 4;
        }

        i += 32;

        for (var desc : font.getMetadata().defaultedDescription().split("\n")) {
            StringBuilder current = new StringBuilder();

            var words = new ArrayList<>(Arrays.asList(desc.split(" ")));

            while (!words.isEmpty()) {
                var word = words.remove(0);
                if (!current.isEmpty() && font.getTextWidth(current + " " + word, 16) > canvas.getWidth() - 32) {
                    font.drawText(canvas, current.toString(), 16, i, 16, CanvasColor.BLACK_HIGH);
                    current = new StringBuilder();
                    i += 16 + 4;
                }

                if (!current.isEmpty()) {
                    current.append(" ");
                }
                current.append(word);
            }

            if (!current.isEmpty()) {
                font.drawText(canvas, current.toString(), 16, i, 16, CanvasColor.BLACK_HIGH);
                i += 16 + 4;
            }
        }

        i = canvas.getHeight() - 128;
        font.drawText(canvas, "The quick brown fox jumps over the lazy dog", 16, i, 16, CanvasColor.BLACK_HIGH);
        i += 16 + 4;
        font.drawText(canvas, "Zażółć gęślą jaźń", 16, i, 16, CanvasColor.BLACK_HIGH);
        i += 16 + 4;

        DefaultFonts.REGISTRY.drawText(canvas, this.userText, 16, i, 32, CanvasColor.BLACK_HIGH, CanvasColor.CLEAR, 2);
        i += 32 + 4;

        var fontId = this.userText.getStyle().getFont() instanceof StyleSpriteSource.Font font1 ? font1.id() : Identifier.of("default");
        DefaultFonts.REGISTRY.getFont(fontId).drawText(canvas, this.userText.getString(), 16, i, 32, CanvasColor.BLACK_HIGH);

        DefaultFonts.VANILLA.drawText(canvas, "" + displayFps, 2, canvas.getHeight() - 10, 8, CanvasColor.GRAY_HIGH);
    }

    @Override
    public void onClick(ServerPlayerEntity player, VirtualDisplay.ClickType type, int x, int y) {

    }

    @Override
    public void onInput(ServerPlayerEntity player, String input) {
        this.userText = TagParser.QUICK_TEXT.parseText(input, ParserContext.of());
        player.sendMessage(this.userText);

    }
}
