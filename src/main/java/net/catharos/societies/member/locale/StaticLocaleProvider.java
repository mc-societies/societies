package net.catharos.societies.member.locale;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.bridge.Player;

import java.util.Locale;

/**
 * Represents a StaticLocaleProvider
 */
class StaticLocaleProvider implements LocaleProvider {

    private final Locale locale;

    @Inject
    public StaticLocaleProvider(@Named("default-locale") Locale locale) {this.locale = locale;}

    @Override
    public Locale provide(Player member) {
        return locale;
    }
}
