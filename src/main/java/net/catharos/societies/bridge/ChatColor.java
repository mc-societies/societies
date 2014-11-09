package net.catharos.societies.bridge;

import gnu.trove.map.hash.THashMap;

import java.util.Map;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;


public enum ChatColor {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f'),
    MAGIC('k', true),
    BOLD('l', true),
    STRIKETHROUGH('m', true),
    UNDERLINE('n', true),
    ITALIC('o', true),
    RESET('r');

    public static final char COLOR_CHAR = '\u00A7';
    public static final char USER_COLOR_CHAR = '&';
    private static final Pattern STRIP_COLOR_PATTERN = pattern(COLOR_CHAR);
    private static final Pattern USER_STRIP_COLOR_PATTERN = pattern(USER_COLOR_CHAR);

    private final char code;
    private final boolean format;
    private final String string;

    private final static Map<Character, ChatColor> BY_CHAR = new THashMap<Character, ChatColor>();

    static {
        for (ChatColor color : values()) {
            BY_CHAR.put(color.code, color);
        }
    }

    private static Pattern pattern(char c) {
        return compile("(?i)" + String.valueOf(c) + "[0-9A-FK-OR]");
    }

    private ChatColor(char code) {
        this(code, false);
    }

    private ChatColor(char code, boolean format) {
        this.code = code;
        this.format = format;
        this.string = new String(new char[]{COLOR_CHAR, code});
    }

    public static void argumentColorReset(Object[] args) {
        for (int i = 0, length = args.length; i < length; i++) {
            Object arg = args[i];

            if (arg instanceof String) {
                args[i] = arg + org.bukkit.ChatColor.RESET.toString();
            }
        }
    }

    public char getChar() {
        return code;
    }

    @Override
    public String toString() {
        return string;
    }

    public boolean isFormat() {
        return format;
    }

    public boolean isColor() {
        return !format && this != RESET;
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }


    public static ChatColor getByChar(String code) {
        return BY_CHAR.get(code.charAt(0));
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

    public static String translateString(String textToTranslate) {
        return translateString('&', textToTranslate);
    }

    public static String translateString(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String getLastColors(String input) {
        String result = "";
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = getByChar(c);

                if (color != null) {
                    result = color.toString() + result;

                    // Once we find a color or reset we can stop searching
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result;
    }
}
