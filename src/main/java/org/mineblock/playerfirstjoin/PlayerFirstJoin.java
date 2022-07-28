package org.mineblock.playerfirstjoin;

import org.bukkit.plugin.java.JavaPlugin;
import org.mineblock.playerfirstjoin.listener.PlayerFirstJoinListener;

public class PlayerFirstJoin extends JavaPlugin {

    public static PlayerFirstJoin INSTANCE;
    public String smile = "Girls with the prettiest smiles, have the saddest stories.";

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        if (PlayerFirstJoin.INSTANCE.getConfig().getBoolean("on-first-join.written-books.enabled")) {
            PlayerFirstJoin.INSTANCE.saveResource("rules.txt", false);
        }
        getServer().getPluginManager().registerEvents(new PlayerFirstJoinListener(this), this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
}
