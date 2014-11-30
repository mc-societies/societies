package org.societies.setting;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import org.societies.bridge.Location;
import net.catharos.lib.shank.AbstractModule;
import org.societies.groups.Relation;
import org.societies.groups.setting.CollectiveSettingProvider;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingProvider;

/**
 * Represents a SettingModule
 */
public class SettingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SettingProvider.class).to(CollectiveSettingProvider.class);

        bind(LocationSetting.ID, "home", new TypeLiteral<Setting<Location>>() {}, LocationSetting.class);
        bind(RelationSetting.ID, new TypeLiteral<Setting<Relation>>() {}, new RelationSetting());
        bind(VerifySetting.ID, "verify", new TypeLiteral<Setting<Boolean>>() {}, new VerifySetting());
        bind(0x4, "personal-friendly-fire", new TypeLiteral<Setting<Boolean>>() {}, new BooleanSetting(0x4));
        bind(0x5, "group-friendly-fire", new TypeLiteral<Setting<Boolean>>() {}, new BooleanSetting(0x4));
        bind(0x6, "group-balance", new TypeLiteral<Setting<Double>>() {}, new DoubleSetting(0x6));
    }

    public <T> void bind(int id, String name, TypeLiteral<Setting<T>> type, Setting<T> setting) {
        bindNamed(name, type).toInstance(setting);
        settings().addBinding(id).toInstance(setting);
    }

    public <T> void bind(int id, String name, TypeLiteral<Setting<T>> type, Class<? extends Setting<T>> setting) {
        bindNamed(name, type).to(setting);
        settings().addBinding(id).to(setting);
    }

    public <T> void bind(int id, TypeLiteral<Setting<T>> type, Setting<T> setting) {
        bind(type).toInstance(setting);
        settings().addBinding(id).toInstance(setting);
    }

    private MapBinder<Integer, Setting> settings() {
        return MapBinder.newMapBinder(binder(), Integer.class, Setting.class, Names.named("settings"));
    }

}
