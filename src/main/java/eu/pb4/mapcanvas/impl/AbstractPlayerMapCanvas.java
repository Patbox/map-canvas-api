package eu.pb4.mapcanvas.impl;

import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPlayerMapCanvas extends BaseMapCanvas implements PlayerCanvas {
    private final int mapId;

    private boolean isDirty = false;
    private int changedMinX = CanvasUtils.MAP_DATA_SIZE;
    private int changedMinY = CanvasUtils.MAP_DATA_SIZE;
    private int changedMaxX = 0;
    private int changedMaxY = 0;
    private boolean isIconsDirty = false;
    private boolean markDestroyed = false;

    public AbstractPlayerMapCanvas(int id) {
        this.mapId = id;
    }

    @Override
    public boolean addPlayer(ServerPlayNetworkHandler player) {
        if (this.markDestroyed) {
            return false;
        }
        if (this.getPlayers().add(player)) {
            this.sendFull(player);
            return true;
        }
        return false;
    }

    protected void sendFull(ServerPlayNetworkHandler player) {
        var icons = new ArrayList<MapIcon>();

        for (var icon : this.icons) {
            if (icon.isVisible()) {
                icons.add(new MapIcon(icon.getType(), (byte) (icon.getX() - 128), (byte) (icon.getY() - 128), icon.getRotation(), icon.getText()));
            }
        }

        player.sendPacket(new MapUpdateS2CPacket(this.mapId, (byte) 0, true, icons, new MapState.UpdateData(0, 0, CanvasUtils.MAP_DATA_SIZE, CanvasUtils.MAP_DATA_SIZE, this.data)));
    }

    @Override
    public int getId() {
        return this.mapId;
    }

    @Override
    public boolean removePlayer(ServerPlayNetworkHandler player) {
        return this.getPlayers().remove(player);
    }

    protected abstract Collection<ServerPlayNetworkHandler> getPlayers();

    @Override
    public boolean isDirty() {
        return this.isDirty || this.isIconsDirty;
    }

    @Override
    protected void markPixelDirty(int x, int y) {
        this.isDirty = true;
        this.changedMinX = Math.min(this.changedMinX, x);
        this.changedMinY = Math.min(this.changedMinY, y);
        this.changedMaxX = Math.max(this.changedMaxX, x);
        this.changedMaxY = Math.max(this.changedMaxY, y);
    }

    @Override
    protected void markIconsDirty() {
        this.isIconsDirty = true;
    }

    @Override
    public void destroy() {
        if (this.markDestroyed) {
            return;
        }
        MapIdManager.freeMapId(this.mapId);
        this.markDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return this.markDestroyed;
    }

    @Override
    public void sendUpdates() {
        if (this.markDestroyed) {
            return;
        }

        MapState.UpdateData pixelData;
        Collection<MapIcon> icons;

        if (this.isDirty) {
            this.isDirty = false;
            final int width = this.changedMaxX - this.changedMinX + 1;
            final int height = this.changedMaxY - this.changedMinY + 1;
            final byte[] bytes = new byte[width * height];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bytes[x + y * width] = this.getRaw(this.changedMinX + x, this.changedMinY + y);
                }
            }

            pixelData = new MapState.UpdateData(this.changedMinX, this.changedMinY, width, height, bytes);

            this.changedMinX = CanvasUtils.MAP_DATA_SIZE;
            this.changedMinY = CanvasUtils.MAP_DATA_SIZE;
            this.changedMaxX = 0;
            this.changedMaxY = 0;
        } else {
            pixelData = null;
        }

        if (this.isIconsDirty) {
            this.isIconsDirty = false;
            icons = new ArrayList<>();
            for (var icon : this.icons) {
                if (icon.isVisible()) {
                    icons.add(new MapIcon(icon.getType(), (byte) (icon.getX() - 128), (byte) (icon.getY() - 128), icon.getRotation(), icon.getText()));
                }
            }
        } else {
            icons = null;
        }

        if (pixelData != null || icons != null) {
            var packet = new MapUpdateS2CPacket(this.mapId, (byte) 0, true, icons, pixelData);
            var players = this.getPlayers();
            synchronized (players) {
                for (var player : players) {
                    if (player.connection.isOpen()) {
                        player.connection.send(packet);
                    }
                }
            }
        }
    }
}
