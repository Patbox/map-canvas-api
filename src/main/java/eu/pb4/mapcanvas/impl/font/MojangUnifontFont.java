package eu.pb4.mapcanvas.impl.font;

import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.impl.font.serialization.RawBitmapFontSerializer;
import eu.pb4.mapcanvas.impl.font.serialization.UniHexFontReader;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;

public final class MojangUnifontFont extends LazyFont {
    private static final Logger LOGGER = LoggerFactory.getLogger("Map Canvas API | Mojang Unifont Loader");

    private final String cachedFile;
    private final String downloadUrl;

    private CompletableFuture<BitmapFont> future;

    public MojangUnifontFont(Metadata metadata, String cachedFile, String downloadUrl) {
        super(metadata);
        this.cachedFile = cachedFile;
        this.downloadUrl = downloadUrl;
    }

    @Override
    public void requestLoad() {
        if (future != null && future.isDone()) {
            try {
                this.font = future.get();
            } catch (Throwable e) {
                this.font = BitmapFont.EMPTY;
                LOGGER.error("Failed to get the font even whe it's done?", e);
            }
            future = null;
            return;
        }
        if (this.isLoaded() || future != null) {
            return;
        }
        var path = FabricLoader.getInstance().getGameDir().resolve(".cache/mapcanvas/" + cachedFile + ".mcaf");
        if (Files.exists(path)) {
            try {
                this.font = RawBitmapFontSerializer.read(Files.newInputStream(path));
                return;
            } catch (Throwable throwable) {
                try {
                    LOGGER.error("Failed to load Map Canvas Font from {}, redownloading...", path, throwable);
                    Files.deleteIfExists(path);
                } catch (Throwable error) {
                    LOGGER.error("Failed to remove the {} file! Using fallback font.", path, error);
                    this.font = BitmapFont.EMPTY;
                    return;
                }
            }
        }

        try {
            var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).followRedirects(HttpClient.Redirect.ALWAYS).build();
            future = client.sendAsync(HttpRequest.newBuilder(URI.create(this.downloadUrl)).GET().build(), HttpResponse.BodyHandlers.ofInputStream())
                    .thenApplyAsync((res) -> {
                        var zip = new ZipInputStream(res.body());
                        try {
                            for (var entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                                if (entry.getName().endsWith(".hex")) {
                                    var font = UniHexFontReader.build(zip, this.getMetadata());
                                    Files.createDirectories(path.getParent());
                                    try (var stream = Files.newOutputStream(path)) {
                                        RawBitmapFontSerializer.write(font, stream);
                                    }
                                    return font;
                                }
                            }
                        } catch (Throwable e) {
                            LOGGER.error("Failed to download or parse {}! Using fallback font.", this.downloadUrl, e);
                            return BitmapFont.EMPTY;
                        }
                        LOGGER.error("Couldn't find required files in {}!", this.downloadUrl);
                        return BitmapFont.EMPTY;
                    }).exceptionally(x -> {
                        LOGGER.error("Failed to download {}! Using fallback font.", this.downloadUrl, x);
                        return BitmapFont.EMPTY;
                    });
        } catch (Throwable e) {
            future = null;
            LOGGER.error("Failed to download {}! Using fallback font.", this.downloadUrl, e);
            this.font = BitmapFont.EMPTY;
        }
    }

    @Override
    protected void waitUntilLoaded() {
        if (this.isLoaded()) {
            if (this.future != null) {
                try {
                    this.font = future.get();
                } catch (Throwable e) {
                    this.font = BitmapFont.EMPTY;
                    LOGGER.error("Failed to get the font even whe it's done?", e);
                }
                this.future = null;
            }
            return;
        }
        this.requestLoad();
        try {
            while (!isLoaded()) {
                Thread.sleep(1);
            }
        } catch (Throwable ignored) {}

        if (future != null && future.isDone()) {
            try {
                this.font = future.get();
            } catch (Throwable e) {
                this.font = BitmapFont.EMPTY;
                LOGGER.error("Failed to get the font even whe it's done?", e);
            }
        }
        future = null;
    }

    @Override
    public boolean isLoaded() {
        return super.isLoaded() || (future != null && future.isDone());
    }
}
