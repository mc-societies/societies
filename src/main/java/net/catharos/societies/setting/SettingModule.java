package net.catharos.societies.setting;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import net.catharos.groups.Relation;
import net.catharos.groups.setting.CollectiveSettingProvider;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingProvider;
import net.catharos.lib.shank.AbstractModule;
import org.bukkit.Location;

/**
 * Represents a SettingModule
 */
public class SettingModule extends AbstractModule {

    @Override
    protected void configure() {

        bindNamed("home", new TypeLiteral<Setting<Location>>() {}).to(LocationSetting.class);

        bind(new TypeLiteral<Setting<Relation>>() {}).to(RelationSetting.class);

        bind(SettingProvider.class).to(CollectiveSettingProvider.class);

        MapBinder<Integer, Setting> settings = MapBinder
                .newMapBinder(binder(), Integer.class, Setting.class, Names.named("settings"));
        settings.addBinding(LocationSetting.ID).to(LocationSetting.class);
        settings.addBinding(RelationSetting.ID).to(RelationSetting.class);
    }


}
