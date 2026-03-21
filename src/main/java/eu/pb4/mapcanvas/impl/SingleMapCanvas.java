package eu.pb4.mapcanvas.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import java.util.ArrayList;

public final class SingleMapCanvas extends AbstractPlayerMapCanvas {
    private final Set<ServerGamePacketListenerImpl> players = Collections.synchronizedSet(new HashSet<>());

    public SingleMapCanvas(int id) {
        super(id);
    }

    @Override
    protected Collection<ServerGamePacketListenerImpl> getPlayers() {
        return this.players;
    }

    @Override
    public void destroy() {
        for (var player : new ArrayList<>(this.players)) {
            this.removePlayer(player);
        }
        super.destroy();
    }
}
