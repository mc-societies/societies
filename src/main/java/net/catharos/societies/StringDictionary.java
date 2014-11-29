package net.catharos.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.bridge.ChatColor;
import net.catharos.lib.core.i18n.DefaultDictionary;

import java.util.Locale;

/**
 * Represents a StringDictionary
 */
public class StringDictionary extends DefaultDictionary<String> {

    @Inject
    public StringDictionary(@Named("default-locale") Locale defaultLocale) {
        super(defaultLocale);
    }

    @Override
    public String getTranslation(String key, String locale, Object... args) {
        translateArguments(args);
        ChatColor.argumentColorReset(args);
        return super.getTranslation(key, locale, args);
    }

    private void translateArguments(Object... args) {
        for (int i = 0, length = args.length; i < length; i++) {
            Object arg = args[i];

            if (arg instanceof String) {
                if (((String) arg).startsWith(":")) {
                    args[i] = getTranslation(((String) arg).substring(1));
                }

                args[i] = args[i] + ChatColor.RESET.toString();
            }
        }
    }
}
