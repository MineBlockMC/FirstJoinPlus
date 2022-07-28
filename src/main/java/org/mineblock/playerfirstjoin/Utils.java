package org.mineblock.playerfirstjoin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Utils {

    public static int getTotalPlayerCount() {
        return PlayerFirstJoin.INSTANCE.getServer().getOfflinePlayers().length;
    }

    public static Location getFirstJoinLocation() {
        World world = PlayerFirstJoin.INSTANCE.getServer().getWorld(PlayerFirstJoin.INSTANCE.getConfig().getString("on-first-join.teleport.world"));
        int x = PlayerFirstJoin.INSTANCE.getConfig().getInt("on-first-join.teleport.x");
        int y = PlayerFirstJoin.INSTANCE.getConfig().getInt("on-first-join.teleport.y");
        int z = PlayerFirstJoin.INSTANCE.getConfig().getInt("on-first-join.teleport.z");
        float yaw = PlayerFirstJoin.INSTANCE.getConfig().getInt("on-first-join.teleport.yaw");
        float pitch = PlayerFirstJoin.INSTANCE.getConfig().getInt("on-first-join.teleport.pitch");
        return new Location(world, x + 0.5, y, z + 0.5, yaw, pitch);
    }

    public static List<ItemStack> getFirstJoinKit() {
        List<ItemStack> kit = new ArrayList<>();
        for (String s : PlayerFirstJoin.INSTANCE.getConfig().getStringList("on-first-join.kits.items")) {
            String[] item = s.split(":");

            Material mat = Material.getMaterial(item[0].toUpperCase());
            if (mat != null) {
                int amount = 1;
                if (item.length > 1) {
                    if (item[1] != null) {
                        amount = Integer.parseInt(item[1]);
                    } else {
                        PlayerFirstJoin.INSTANCE.getLogger().log(Level.WARNING, "Not valid number: " + item[1]);
                    }
                }
                ItemStack is = new ItemStack(mat, amount);
                kit.add(is);
            } else {
                PlayerFirstJoin.INSTANCE.getLogger().log(Level.WARNING, "Unknown item name: " + item[0]);
            }
        }
        return kit;
    }

    public static List<ItemStack> getWrittenBooks(Player viewingPlayer) {
        List<ItemStack> books = new ArrayList<>();
        for (String file : PlayerFirstJoin.INSTANCE.getConfig().getStringList("on-first-join.written-books.book-files")) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) book.getItemMeta();
            File f = new File(PlayerFirstJoin.INSTANCE.getDataFolder(), file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                int i = 0;

                while (line != null) {
                    i++;
                    line = replacePlaceholders(line, viewingPlayer);
                    if (i != 1 && i != 2) {
                        if (line.equalsIgnoreCase("/newpage") || line.equalsIgnoreCase("/np")) {
                            bm.addPage(sb.toString());
                            sb = new StringBuilder();
                        } else {
                            sb.append(ChatColor.translateAlternateColorCodes('&', line));
                            sb.append("\n");
                        }
                    } else {
                        if (i == 1) {
                            bm.setTitle(ChatColor.translateAlternateColorCodes('&', line));
                        }
                        if (i == 2) {
                            bm.setAuthor(ChatColor.translateAlternateColorCodes('&', line));
                        }
                    }
                    line = br.readLine();
                }

                br.close();
                bm.addPage(sb.toString());
            } catch (Exception e) {
                PlayerFirstJoin.INSTANCE.getLogger().log(Level.WARNING, "Failed to give a new player a written book!");
                PlayerFirstJoin.INSTANCE.getLogger().log(Level.WARNING, "File: " + file);
                PlayerFirstJoin.INSTANCE.getLogger().log(Level.WARNING, "Reason: " + e);
            }

            book.setItemMeta(bm);
            books.add(book);
        }
        return books;
    }

    public static String replacePlaceholders(String string, Player player) {
        string = string.replace("%player_name%", player.getName())
                .replace("%player_display_name%", player.getDisplayName())
                .replace("%player_uuid%", player.getUniqueId().toString())
                .replace("%total_players%", getTotalPlayerCount() + "")
                .replace("%new_line%", "\n");
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
