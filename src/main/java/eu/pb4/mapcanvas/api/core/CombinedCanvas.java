package eu.pb4.mapcanvas.api.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface CombinedCanvas {
    @Nullable DrawableCanvas getSubCanvas(int x, int y);

    int getSectionsWidth();

    int getSectionsHeight();

    void resize(int width, int height);
}
