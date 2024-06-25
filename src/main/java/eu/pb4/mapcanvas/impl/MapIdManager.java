package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.mixin.EntityAccessor;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntSet;

public class MapIdManager {
    private static int currentMapId = -10;
    private static final IntArrayList freeEntityIds = new IntArrayList();
    private static final IntArrayList freeMapIds = new IntArrayList();


    public static int requestEntityId() {
        if (!freeEntityIds.isEmpty()) {
            return freeEntityIds.popInt();
        }
        return EntityAccessor.getCurrentId().incrementAndGet();
    }



    public static int requestMapId() {
        if (!freeMapIds.isEmpty()) {
            return freeMapIds.popInt();
        }
        return currentMapId--;
    }

    public static void freeMapId(int mapId) {
        if (!freeMapIds.contains(mapId)) {
            freeMapIds.push(mapId);
        }
    }

    public static void freeEntityId(int entityId) {
        if (!freeEntityIds.contains(entityId)) {
            freeEntityIds.push(entityId);
        }
    }
}
