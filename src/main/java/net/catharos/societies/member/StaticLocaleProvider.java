package net.catharos.societies.member;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Locale;

/**
 * Represents a StaticLocaleProvider
 */
public class StaticLocaleProvider implements LocaleProvider {

    private final Locale locale;

    @Inject
    public StaticLocaleProvider(@Named("default-locale") Locale locale) {this.locale = locale;}

    @Override
    public Locale provide(SocietyMember member) {
        return locale;
    }
}
