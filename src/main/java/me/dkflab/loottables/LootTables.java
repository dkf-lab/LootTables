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

    public void removeCommandFromItem(String itemID) {
        getConfig().set("commands." + itemID, null);
        saveConfig();
    }

    public void removeWeightFromItem(String tableID, String itemID) {
        getConfig().set("weight." + tableID + "." + itemID, null);
        saveConfig();
    }

    public void addItemToConfig(@NotNull String itemID, @NotNull ItemStack item) {
        getConfig().set("items." + itemID, item);
        saveConfig();
    }

    public boolean lootTableContainsWeights(String lootTable) {
        // does the loot table have percentages??
        Set<String> list = getItemsFromTable(lootTable).keySet();
        for (String s : list) {
            if (getPercentage(lootTable, s) == 0) {
                return false; // no percentages
            }
        }
        return true;
    }

    public void removeItemFromConfig(String itemID) {
        // remove item from loot tables
        ConfigurationSection sec = getConfig().getConfigurationSection("loottables");
        if (sec != null) {
            String tableList = getLootTableFromItem(itemID);
            if (tableList != null) {
                List<String> list = sec.getStringList(tableList);
                list.remove(itemID);
                sec.set(tableList,list);
            }
        }
        // remove item from item list
        getConfig().set("items." + itemID, null);
        saveConfig();
    }

    public String getLootTableFromItem(String itemID) {
        for (String table : tablesList()) {
            if (getItemsFromTable(table).containsKey(itemID)) {
                return table;
            }
        }
        return null;
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

    public int getPercentage(String tableID, String itemID) {
        int percentage = 0;
        if (itemExists(itemID)) {
            try {
                percentage = getConfig().getInt("weight." + tableID + "." + itemID);
            } catch (Exception ignored) { }
        }
        return percentage;
    }

    public void setPercentage(String tableID, String itemID, int percentage) {
        getConfig().set("weight." + tableID + "." + itemID, percentage);
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

    public boolean percentagesAddUpTo100(String tableID) {
        int chance = 0;
        for (String item : getItemsFromTable(tableID).keySet()) {
            chance += getPercentage(tableID, item);
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
