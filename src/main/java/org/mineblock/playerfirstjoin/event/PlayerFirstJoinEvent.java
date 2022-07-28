package org.mineblock.playerfirstjoin.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.mineblock.playerfirstjoin.PlayerFirstJoin;
import org.mineblock.playerfirstjoin.Utils;

public class PlayerFirstJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final PlayerJoinEvent event;

    public PlayerFirstJoinEvent(PlayerJoinEvent event) {
        this.event = event;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return event.getPlayer();
    }

    public void setJoinMessage(String message) {
        event.setJoinMessage(message);
    }

    public Location getSpawnLocation() {
        if (PlayerFirstJoin.INSTANCE.getConfig().getBoolean("on-first-join.teleport.enabled")) {
            return Utils.getFirstJoinLocation();
        } else {
            return event.getPlayer().getLocation();
        }
    }
}
