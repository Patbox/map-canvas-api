package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface PlayerInterface {
    void mapcanvas_addDisplay(IntList ids, VirtualDisplay display, @Nullable Box box);
    void mapcanvas_removeDisplay(IntList ids, VirtualDisplay display);

    VirtualDisplay mapcanvas_getDisplay(int id);
    Set<Map.Entry<VirtualDisplay, Box>> mapcanvas_getBoxes();
}
