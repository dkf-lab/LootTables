package me.dkflab.loottables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                        String id = args[1];
                        try {
                            if (!main.getConfig().getConfigurationSection("items").contains(id)) {
                                sendMessage(sender, "&c&lError! &e" + id + "&7 is an invalid ID.");
                                return true;
                            }
                        } catch (Exception e) {
                            sendMessage(sender, "&c&lError! &e" + id + "&7 is an invalid ID.");
                            return true;
                        }
                        // args parsed
                        StringBuilder s = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            if (i == 2) {
                                s.append(args[i]);
                            } else {
                                s.append(" " + args[i]);
                            }
                        }
                        main.setCommandOfItem(id,s.toString());
                        sendMessage(sender,"&a&lSuccess! &7Added command &e" +s+ "&7 to item &e" + id + "&7!");
                        return true;
                    }
                }
            }
            // ADD ITEM
            if (args.length == 3) {
                if (!args[0].equalsIgnoreCase("additem")) {
                    help(sender);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sendMessage(sender, "&c&lError! &7You need to be a player to run that command.");
                    return true;
                }
                String itemID = args[1];
                String tableID = args[2];
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
            // GIVE
            if (args.length == 4) {
                if (!args[0].equals("give")) {
                    help(sender);
                    return true;
                }
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
                // all args parsed
                List<ItemStack> itemStackList = new ArrayList<>(map.values());
                List<String> idList = new ArrayList<>(map.keySet());
                for (int i = 0; i < quantity; i++) {
                    Random rand = new Random();
                    String randomId = idList.get(rand.nextInt(idList.size()));
                    p.getInventory().addItem(map.get(randomId));
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
        }
        help(sender);
        return true;
    }

    private void help(CommandSender sender) {
        sendMessage(sender, "&aLootTables Help");
        sendMessage(sender, "&8/lt &e<help | additem | give | cmd> &7- Base commands");
        sendMessage(sender, "&8/lt &eadditem <id> <table> &7- Adds item to config under 'id' and adds to table");
        sendMessage(sender, "&8/lt &egive <player> <table> <quantity> &7- Gives players items from loot table.");
        sendMessage(sender, "&8/lt &ecmd <id> <command> &7- Add command to item. Use %player% to reference player.");
    }
}
