package eu.pb4.mapcanvas.testmod.advancedgui;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

public class BrowserTestRenderer implements ActiveRenderer {
    public ChromeDriver driver;
    int x = 0;
    int y = 0;
    private boolean lastClick;

    @Override
    public void setup(PlayerCanvas canvas) {

    }

    @Override
    public void render(PlayerCanvas outputCanvas, DrawableCanvas canvas, long time, int displayFps, int frame) {
        try {
            CanvasUtils.clear(canvas, CanvasColor.LIGHT_GRAY_HIGH);
            var image = ImageIO.read(new ByteArrayInputStream(this.driver.getScreenshotAs(OutputType.BYTES)));
            CanvasUtils.draw(canvas, 0, 32, CanvasImage.from(image));
            DefaultFonts.VANILLA.drawText(canvas, this.driver.getCurrentUrl(),16, 8, 16, CanvasColor.BLACK_HIGH);

            CanvasUtils.fill(canvas, this.x - 2, this.y - 2, this.x + 2, this.y + 2, CanvasColor.BLACK_LOW);
            CanvasUtils.fill(canvas, this.x - 1, this.y - 1, this.x + 1, this.y + 1, this.lastClick ? CanvasColor.RED_HIGH : CanvasColor.GREEN_HIGH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(ServerPlayerEntity player, ClickType type, int x, int y) {
        try {
            var mouse = new PointerInput(PointerInput.Kind.MOUSE, "default mouse");

            var button = type == ClickType.LEFT ? PointerInput.MouseButton.LEFT.asArg() : PointerInput.MouseButton.RIGHT.asArg();

            var actions = new Sequence(mouse, 0)
                    .addAction(mouse.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y - 32))
                    .addAction(mouse.createPointerDown(button))
                    .addAction(new Pause(mouse, Duration.ofMillis(20)))
                    .addAction(mouse.createPointerUp(button));

            this.driver.perform(Collections.singletonList(actions));

            this.x = x;
            this.y = y;
            this.lastClick = type == ClickType.LEFT;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInput(String input) {
        new Actions(this.driver).sendKeys(input).perform();
    }

    @Override
    public void setStatus(boolean active) {
        if (active) {
            System.out.println("Testing...");
            var options = new ChromeOptions();
            options.setBinary("/usr/bin/google-chrome-stable");
            options.setLogLevel(ChromeDriverLogLevel.OFF);
            options.addArguments("--headless");
            this.driver = new ChromeDriver(options);
            this.driver.get("https://pb4.eu/");
            this.driver.manage().window().setSize(new Dimension(6 * 128, 4 * 128 - 32));
        } else {
            if (this.driver != null) {
                this.driver.quit();
            }
        }
    }
}
