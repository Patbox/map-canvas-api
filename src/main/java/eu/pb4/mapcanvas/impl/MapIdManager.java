package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.mixin.EntityAccessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

public class MapIdManager {
    private static int currentMapId = -1;
    private static final IntList freeEntityIds = new IntArrayList();
    private static final IntList freeMapIds = new IntArrayList();


    public static int requestEntityId() {
        if (freeEntityIds.size() != 0) {
            return freeEntityIds.removeInt(0);
        }
        return EntityAccessor.getCurrentId().incrementAndGet();
    }



    public static int requestMapId() {
        if (freeMapIds.size() != 0) {
            return freeMapIds.removeInt(0);
        }
        return currentMapId--;
    }

    public static void freeMapId(int mapId) {
        if (!freeMapIds.contains(mapId)) {
            freeMapIds.add(mapId);
        }
    }

    public static void freeEntityId(int entityId) {
        if (!freeEntityIds.contains(entityId)) {
            freeEntityIds.add(entityId);
        }
    }
}
