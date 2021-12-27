package me.dkflab.loottables;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.util.*;

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

    public boolean lootTableContainsWeights(String lootTable) {
        // does the loot table have percentages??
        Set<String> list = getItemsFromTable(lootTable).keySet();
        for (String s : list) {
            if (getPercentage(s) == 0) {
                return false; // no percentages
            }
        }
        return true;
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

    public List<String> itemsList() {
        List<String> list = new ArrayList<>();
        try {
           list.addAll(getConfig().getConfigurationSection("items").getKeys(false));
        } catch (Exception ignored) {

        }
        return list;
    }

    public boolean itemExists(String itemID) {
        return itemsList().contains(itemID);
    }

    public boolean tableExists(String tableID) {
        return tablesList().contains(tableID);
    }

    public int getPercentage(String itemID) {
        int percentage = 0;
        if (itemExists(itemID)) {
            try {
                percentage = getConfig().getConfigurationSection("weight").getInt(itemID);
            } catch (Exception ignored) { }
        }
        return percentage;
    }

    public void setPercentage(String itemID, int percentage) {
        getConfig().set("weight." + itemID, percentage);
        saveConfig();
    }

    public List<String> tablesList() {
        List<String> list = new ArrayList<>();
        try {
           list.addAll(getConfig().getConfigurationSection("loottables").getKeys(false));
        } catch (Exception ignored) {

        }
        return list;
    }

    public boolean percentagesAddUpTo100(Set<String> set) {
        // presuming Set<String> is a set of item ids
        int chance = 0;
        for (String s : set) {
            chance += getPercentage(s);
        }
        return chance == 100;
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
