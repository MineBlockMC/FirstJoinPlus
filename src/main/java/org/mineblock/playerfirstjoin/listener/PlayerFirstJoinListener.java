package org.mineblock.playerfirstjoin.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineblock.playerfirstjoin.PlayerFirstJoin;
import org.mineblock.playerfirstjoin.Utils;
import org.mineblock.playerfirstjoin.event.PlayerFirstJoinEvent;

public class PlayerFirstJoinListener implements Listener {

    private final PlayerFirstJoin plugin;

    public PlayerFirstJoinListener(PlayerFirstJoin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void firstJoinDetection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean isPlayerExist;

        if(plugin.getConfig().getBoolean("settings.every-join-is-first-join")){
            isPlayerExist = false;
        } else {
            isPlayerExist = player.hasPlayedBefore();
        }

        if (!isPlayerExist) {
            plugin.getServer().getPluginManager().callEvent(new PlayerFirstJoinEvent(event));
        }
    }

    @EventHandler
    public void onFirstJoin(final PlayerFirstJoinEvent event) {
        final Player player = event.getPlayer();

        if (plugin.getConfig().getBoolean("on-first-join.broadcast-message.enabled"))
            event.setJoinMessage(Utils.replacePlaceholders(plugin.getConfig().getString("on-first-join.broadcast-message.message"), player));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getConfig().getBoolean("on-first-join.teleport.enabled"))
                    player.teleport(event.getSpawnLocation());

                if (plugin.getConfig().getBoolean("on-first-join.kits.enabled"))
                    Utils.getFirstJoinKit().forEach(i -> player.getInventory().addItem(i));

                if (plugin.getConfig().getBoolean("on-first-join.written-books.enabled"))
                    Utils.getWrittenBooks(player).forEach(i -> player.getInventory().addItem(i));

                if (plugin.getConfig().getBoolean("on-first-join.run-commands.enabled"))
                    plugin.getConfig().getStringList("on-first-join.run-commands.commands").forEach(s -> player.performCommand(Utils.replacePlaceholders(s, player)));

                if (plugin.getConfig().getBoolean("on-first-join.run-console-commands.enabled"))
                    plugin.getConfig().getStringList("on-first-join.run-console-commands.commands").forEach(s -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Utils.replacePlaceholders(s, player)));
            }
        }.runTask(plugin);
    }
}
