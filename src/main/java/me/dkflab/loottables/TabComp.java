package me.dkflab.loottables;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabComp implements TabCompleter {

    private LootTables main;
    public TabComp(LootTables main) {
        this.main = main;
    }
    List<String> arguments = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("item");
            arguments.add("give");
            arguments.add("cmd");
            arguments.add("table");
            arguments.add("list");
            arguments.add("weight");
        }
        List<String> result = new ArrayList<String>();
        // Command override
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("cmd")) {
                result.add("<command>");
                return result;
            }
        }
        // Automatic
        if (args.length == 1) {
            for (String a : arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        if (args.length == 2) {
            String s = args[0];
            if (s.equalsIgnoreCase("weight")) {
                result.addAll(main.itemsList());
                if (result.isEmpty()) {
                    result.add("<id>");
                }
            }
            if (s.equalsIgnoreCase("table")) {
                result.add("create");
                result.add("remove");
            }
            if (s.equalsIgnoreCase("list")) {
                result.add("tables");
                result.add("items");
                result.add("all");
            }
            if (s.equalsIgnoreCase("item")) {
                result.add("add");
                result.add("remove");
            }
            if (s.equalsIgnoreCase("give")) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    result.add(all.getName());
                }
            }
            if (s.equalsIgnoreCase("cmd")) {
                result.addAll(main.itemsList());
                if (result.isEmpty()) {
                    result.add("<id>");
                }
            }
            return result;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("weight")) {
                for (int i = 0; i < 101; i++) {
                    result.add(""+i);
                }
            }
            if (args[0].equalsIgnoreCase("item")) {
                result.addAll(main.itemsList());
                if (result.isEmpty()) {
                    result.add("<id>");
                }
            }
            if (args[0].equalsIgnoreCase("table")) {
                if (args[1].equalsIgnoreCase("remove")) {
                    result.addAll(main.tablesList());
                }
                if (result.isEmpty()) {
                    result.add("<id>");
                }
            }
            if (args[0].equalsIgnoreCase("give")) {
                result.addAll(main.tablesList());
                if (result.isEmpty()) {
                    result.add("<table>");
                }
            }
            return result;
        }
        if (args.length == 4) {
            if (args[0].equals("give")) {
                for (int i = 0; i < 10; i++) {
                    result.add(""+i);
                }
            }
            if (args[0].equalsIgnoreCase("item")) {
                if (args[1].equalsIgnoreCase("remove")) {
                    return result;
                }
                result.addAll(main.tablesList());
                if (result.isEmpty()) {
                    result.add("<table>");
                }
            }
            return result;
        }
        return null;
    }
}
