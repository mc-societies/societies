package net.catharos.societies.bukkit;

import org.bukkit.ChatColor;

/**
 * Represents a BukkitUtil
 */
public class BukkitUtil {

    public static void argumentColorReset(Object[] args) {
        for (int i = 0, length = args.length; i < length; i++) {
            Object arg = args[i];

            if (arg instanceof String) {
                args[i] = arg + ChatColor.RESET.toString();
            }
        }
    }
}
