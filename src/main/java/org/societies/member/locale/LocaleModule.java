package org.societies.member.locale;

import org.shank.AbstractModule;

import java.util.Locale;

/**
 * Represents a LocaleModule
 */
public class LocaleModule extends AbstractModule {


    private final Locale defaultLocale;

    public LocaleModule(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    protected void configure() {
        bind(LocaleProvider.class).to(StaticLocaleProvider.class);

        bindNamedInstance("default-locale", Locale.class, defaultLocale);
    }
}
