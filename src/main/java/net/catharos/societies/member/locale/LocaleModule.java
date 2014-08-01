package net.catharos.societies.member.locale;

import net.catharos.lib.shank.AbstractModule;

import java.util.Locale;

/**
 * Represents a LocaleModule
 */
public class LocaleModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LocaleProvider.class).to(DynamicLocaleProvider.class);
        bindNamed("default-locale", LocaleProvider.class).to(DynamicLocaleProvider.class);
        bindNamedInstance("default-locale", Locale.class, Locale.US);
    }
}
