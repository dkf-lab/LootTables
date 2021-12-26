package me.dkflab.loottables;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class LootTables extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("loottable").setExecutor(new Commands(this));
        getCommand("loottable").setTabCompleter(new TabComp(this));
    }

    public void addItemToConfig(@NotNull String itemID, @NotNull ItemStack item) {
        getConfig().set("items." + itemID, item);
        saveConfig();
    }

    public void addItemToLootTable(String itemID, String lootTable) {
        ConfigurationSection sec = getConfig().getConfigurationSection("loottables");
        if (sec == null) {
            getConfig().set("loottables." + lootTable, Collections.singletonList(itemID));
        } else {
            List<String> list = sec.getStringList(lootTable);
            list.add(itemID);
            sec.set(lootTable,list);
        }
        saveConfig();
    }

    public HashMap<String, ItemStack> getItemsFromTable(String id) {
        // map is id,item
        HashMap<String,ItemStack> map = new HashMap<>();
        try {
            List<String> itemIDs = getConfig().getConfigurationSection("loottables").getStringList(id);
            for (String s : itemIDs) {
                map.put(s,getItemFromID(s));
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Loot tables section doesn't exist.");
        }
        return map;
    }

    public ItemStack getItemFromID(String id) {
        return getConfig().getConfigurationSection("items").getItemStack(id);
    }

    public void setCommandOfItem(String id, String command) {
        getConfig().set("commands." + id, command);
        saveConfig();
    }

    public String getCommandFromItem(String id) {
        String s = getConfig().getString("commands." + id);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }
}
