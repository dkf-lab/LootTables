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
            arguments.add("additem");
            arguments.add("give");
            arguments.add("cmd");
        }
        List<String> result = new ArrayList<String>();
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("cmd")) {
                result.add("<command>");
                return result;
            }
        }
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
            if (s.equalsIgnoreCase("additem")) {
                result.add("<id>");
            }
            if (s.equalsIgnoreCase("give")) {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    result.add(all.getName());
                }
            }
            if (s.equalsIgnoreCase("cmd")) {
                try {
                    result.addAll(main.getConfig().getConfigurationSection("items").getKeys(false));
                } catch (Exception e) {
                    result.add("<id>");
                }
            }
            return result;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("additem")) {
                try {
                    result.addAll(main.getConfig().getConfigurationSection("loottables").getKeys(false));
                } catch (Exception ignored) {
                    Bukkit.getLogger().warning("Loot tables configuration section is empty");
                }
                if (result.isEmpty()) {
                    result.add("<table>");
                }
            }
            if (args[0].equalsIgnoreCase("give")) {
                try {
                    result.addAll(main.getConfig().getConfigurationSection("loottables").getKeys(false));
                } catch (Exception ignored) {
                    Bukkit.getLogger().warning("Loot tables configuration section is empty");
                }
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
            return result;
        }
        return null;
    }
}
