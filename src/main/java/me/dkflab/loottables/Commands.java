package me.dkflab.loottables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.dkflab.loottables.Utils.*;

public class Commands implements CommandExecutor {

    private LootTables main;
    public Commands(LootTables main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull java.lang.String label, @NotNull java.lang.String[] args) {
        if (command.getName().equalsIgnoreCase("loottable")) {
            // CMD OVERRIDE CHECK
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("cmd")) {
                    if (args.length <= 2) {
                        help(sender);
                        return true;
                    } else {
                        String itemID = args[2];
                        if (!main.itemExists(itemID)) {
                            sendMessage(sender, "&c&LError! &e" + itemID + " &7does not exist.");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("add")) {
                            if (args.length <= 3) {
                                help(sender);
                                return true;
                            }
                            StringBuilder s = new StringBuilder();
                            for (int i = 3; i < args.length; i++) {
                                if (i == 3) {
                                    s.append(args[i]);
                                } else {
                                    s.append(" " + args[i]);
                                }
                            }
                            main.setCommandOfItem(itemID,s.toString());
                            sendMessage(sender,"&a&lSuccess! &7Added command &e" +s+ "&7 to item &e" + itemID + "&7!");
                            return true;
                        } else if (args[1].equalsIgnoreCase("remove")) {
                            main.removeCommandFromItem(itemID);
                            sendMessage(sender,"&a&lSuccess! &7Removed command from item &e" + itemID + "&7.");
                            return true;
                        } else {
                            help(sender);
                            return true;
                        }
                    }
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (args[1].equalsIgnoreCase("tables")) {
                        List<String> list = new ArrayList<>();
                        try {
                            list.addAll(main.getConfig().getConfigurationSection("loottables").getKeys(false));
                        } catch (Exception ignored) {

                        }
                        if (list.isEmpty()) {
                            sendMessage(sender, "&c&lError! &7No tables found.");
                        } else {
                            sendMessage(sender, "&a&lList of Tables:");
                            for (String s : list) {
                                sendMessage(sender, "&f - " + s);
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("items")) {
                        List<String> list = new ArrayList<>();
                        try {
                            list.addAll(main.getConfig().getConfigurationSection("items").getKeys(false));
                        } catch (Exception ignored) {

                        }
                        if (list.isEmpty()) {
                            sendMessage(sender, "&c&lError! &7No items found.");
                        } else {
                            sendMessage(sender, "&a&lList of Items:");
                            for (String s : list) {
                                sendMessage(sender, "&f - " + s);
                            }
                        }
                    } else if (args[1].equalsIgnoreCase("all")) {
                        List<String> list = new ArrayList<>();
                        try {
                            list.addAll(main.getConfig().getConfigurationSection("loottables").getKeys(false));
                        } catch (Exception ignored) {

                        }
                        if (list.isEmpty()) {
                            sendMessage(sender, "&c&lError! &7No tables found.");
                        } else {
                            sendMessage(sender, "&a&lList of Tables & Items:");
                            for (String s : list) {
                                sendMessage(sender, "&f - " + s);
                                for (String item : main.getItemsFromTable(s).keySet()) {
                                    sendMessage(sender, "&7  - " + item);
                                }
                            }
                        }
                    } else {
                        help(sender);
                    }
                    return true;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("table")) {
                    if (args[1].equalsIgnoreCase("remove")) {
                        try {
                            if (main.tablesList().contains(args[2])) {
                                main.getConfig().getConfigurationSection("loottables").set(args[2], null);
                                main.saveConfig();
                                sendMessage(sender, "&a&lSuccess! &7Table &e" + args[2] + "&7 has been removed.");
                                return true;
                            }
                        } catch (Exception ignored) { }
                        sendMessage(sender, "&c&lError! &e" + args[2] + " &7is not a valid ID.");
                        return true;
                    } else if (args[1].equalsIgnoreCase("create")) {
                        main.getConfig().set("loottables." + args[2], null);
                        main.saveConfig();
                        sendMessage(sender, "&a&lSuccess! &7Created table under ID &e" + args[2]);
                    } else {
                        help(sender);
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("item")) {
                    if (args[1].equalsIgnoreCase("remove")) {
                        String itemID = args[2];
                        if (main.itemsList().contains(itemID)) {
                            main.removeItemFromConfig(itemID);
                            sendMessage(sender, "&a&lSuccess! &7Item ID &e" + itemID + " &7has been removed.");
                        } else {
                            sendMessage(sender, "&c&lError! &e" + itemID + "&7 does not exist.");
                        }
                        return true;
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equals("give")) {
                    Player p = null;
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        if (all.getName().equalsIgnoreCase(args[1])) {
                            p = all;
                        }
                    }
                    if (p == null) {
                        sendMessage(sender, "&c&lError! &e" + args[1] +" &7is not a player name. Check spelling and try again.");
                        return true;
                    }

                    // get list of items
                    // Map is ID,ITEM
                    HashMap<String,ItemStack> map = main.getItemsFromTable(args[2]);
                    if (map.isEmpty()) {
                        sendMessage(sender, "&c&lError! &e" + args[2] + " &7is not a valid loot table ID.");
                        return true;
                    }

                    // quantity
                    int quantity = 0;
                    try {
                        quantity = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sendMessage(sender, "&c&lError! &7Invalid quantity.");
                        return true;
                    }
                    if (quantity == 0) {
                        sendMessage(sender, "&c&lError! &7Invalid quantity.");
                        return true;
                    }
                    RandomCollection collection = new RandomCollection<>();
                    for (String s : map.keySet()) {
                        collection = collection.add(main.getPercentage(args[2],s),s);
                    }
                    // all args parsed
                    List<String> idList = new ArrayList<>(map.keySet());
                    for (int i = 0; i < quantity; i++) {
                        // choose random item
                        // check if items have weight
                        String randomId;
                        if (main.lootTableContainsWeights(args[2])) {
                            randomId = (String) collection.next();
                            if (!main.percentagesAddUpTo100(args[2])) {
                                sendMessage(sender, "&c&lWARNING! &7Your percentages do not add up to &e100%&7!");
                            }
                        } else {
                            Random rand = new Random();
                            randomId = idList.get(rand.nextInt(idList.size()));
                        }
                        // ADD ITEM TO INV
                        p.getInventory().addItem(map.get(randomId));
                        // COMMANDS
                        String cmd = main.getCommandFromItem(randomId).replace("%player%", p.getName());
                        if (cmd.length() != 0) {
                            if (String.valueOf(cmd.charAt(0)).equals("/")) {
                                cmd = cmd.substring(1);
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
                        }
                    }
                    sendMessage(sender, "&a&lSuccess! &7Given item to &e" + p.getName());
                    return true;
                }
                if (args[0].equalsIgnoreCase("item")) {
                    if (args[1].equalsIgnoreCase("add")) {
                        if (!(sender instanceof Player)) {
                            sendMessage(sender, "&c&lError! &7You need to be a player to run that command.");
                            return true;
                        }
                        String itemID = args[2];
                        String tableID = args[3];
                        Player p = (Player)sender;
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item.getType().equals(Material.AIR)) {
                            sendMessage(p, "&c&lError! &7You need to hold an item in your hand.");
                            return true;
                        }
                        // all args parsed
                        main.addItemToConfig(itemID, item);
                        main.addItemToLootTable(itemID, tableID);
                        sendMessage(p, "&a&lSuccess! &7Added item under ID &e" + itemID + "&7 and to loot table &e" + tableID);
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        String itemID = args[2];
                        if (main.itemsList().contains(itemID)) {
                            main.removeItemFromConfig(itemID);
                            sendMessage(sender, "&a&lSuccess! &7Item ID &e" + itemID + " &7has been removed.");
                        } else {
                            sendMessage(sender, "&c&lError! &e" + itemID + "&7 does not exist.");
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("weight")) {
                    // remove weight only
                    if (!args[1].equalsIgnoreCase("remove")) {
                        help(sender);
                        return true;
                    }
                    String tableID = args[2];
                    String itemID = args[3];
                    if (!main.tableExists(tableID)) {
                        sendMessage(sender, "&c&LError! &e" + tableID + " &7does not exist.");
                        return true;
                    }
                    if (!main.itemExists(itemID)) {
                        sendMessage(sender, "&c&LError! &e" + itemID + " &7does not exist.");
                        return true;
                    }
                    main.removeWeightFromItem(tableID,itemID);
                    sendMessage(sender, "&a&lSuccess! &7Weight removed from table &e" + tableID + "&7 and item &e" + itemID + "&7.");
                    return true;
                }
            }
            if (args.length == 5) {
                if (args[0].equalsIgnoreCase("weight")) {
                    // add weight only
                    String tableID = args[2];
                    String itemID = args[3];
                    if (!args[1].equalsIgnoreCase("add")) {
                        help(sender);
                        return true;
                    }
                    if (!main.tableExists(tableID)) {
                        sendMessage(sender, "&c&LError! &e" + tableID + " &7does not exist.");
                        return true;
                    }
                    if (!main.itemExists(itemID)) {
                        sendMessage(sender, "&c&LError! &e" + itemID + " &7does not exist.");
                        return true;
                    }
                    int percentage;
                    try {
                        percentage = Integer.parseInt(args[4]);
                    } catch (NumberFormatException e) {
                        sendMessage(sender, "&c&LError! &e" + args[4] +"&7 is not an integer.");
                        return true;
                    }
                    // all variables parsed
                    main.setPercentage(tableID, itemID, percentage);
                    sendMessage(sender, "&a&lSuccess! &e" + itemID + "&7 has a percentage weight of &e" + percentage + "%&7!");
                    return true;
                }
            }
        }
        help(sender);
        return true;
    }

    private void help(CommandSender sender) {
        sendMessage(sender, "&aLootTables Help");
        sendMessage(sender, "&8/lt &eitem <add | remove> <id> <table> &7- Adds item to config under 'id' and adds to table");
        sendMessage(sender, "&8/lt &egive <player> <table> <quantity> &7- Gives players items from loot table.");
        sendMessage(sender, "&8/lt &etable <create | remove> <id> &7- Creates a loot table");
        sendMessage(sender, "&8/lt &ecmd <add | remove> <id> <command> &7- Add command to item. Use %player% to reference player.");
        sendMessage(sender, "&8/lt &elist <table | items | all> &7- List current tables and items in the config.");
        sendMessage(sender, "&8/lt &eweight <add | remove> <table> <id> <percentage> &7- Set percentage chance of getting an item.");
    }
}
