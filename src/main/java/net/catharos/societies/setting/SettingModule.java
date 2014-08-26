package net.catharos.societies.setting;

import net.catharos.groups.setting.CollectiveSettingProvider;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a SettingModule
 */
public class SettingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SettingProvider.class).to(CollectiveSettingProvider.class);
    }
}
