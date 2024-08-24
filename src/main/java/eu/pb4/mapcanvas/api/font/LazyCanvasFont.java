package eu.pb4.mapcanvas.api.font;

public interface LazyCanvasFont extends CanvasFont {
    boolean isLoaded();
    void requestLoad();
}
