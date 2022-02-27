package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import it.unimi.dsi.fastutil.ints.IntList;

public interface PlayerInterface {
    void mapcanvas_addDisplay(IntList ids, VirtualDisplay display);
    void mapcanvas_removeDisplay(IntList ids);

    VirtualDisplay mapcanvas_getDisplay(int id);
}
