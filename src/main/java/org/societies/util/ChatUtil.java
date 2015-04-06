package org.societies.util;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Represents a ChatUtil
 */
public class ChatUtil {

    public static final char COLOR_CHAR = '\u00A7';
    public static final char USER_COLOR_CHAR = '&';
    private static final Pattern STRIP_COLOR_PATTERN = pattern(COLOR_CHAR);
    private static final Pattern USER_STRIP_COLOR_PATTERN = pattern(USER_COLOR_CHAR);

    private static Pattern pattern(char c) {
        return compile("(?i)" + String.valueOf(c) + "[0-9A-FK-OR]");
    }


    public static void argumentColorReset(Object[] args) {
        for (int i = 0, length = args.length; i < length; i++) {
            Object arg = args[i];

            if (arg instanceof String) {
                args[i] = arg + ChatColor.RESET.toString();
            }
        }
    }

    public static String stripColor(final String input) {
        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String stripUserColor(final String input) {
        if (input == null) {
            return null;
        }

        return USER_STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

}
