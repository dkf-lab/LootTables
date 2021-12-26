package me.dkflab.loottables;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static void sendMessage(CommandSender s, String msg) {
        s.sendMessage(color(msg));
    }
}
