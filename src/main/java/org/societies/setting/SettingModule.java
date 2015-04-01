package org.societies.setting;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import org.shank.AbstractModule;
import org.societies.api.setting.LocationSetting;
import org.societies.api.setting.RelationSetting;
import org.societies.api.setting.VerifiedSetting;
import org.societies.bridge.Location;
import org.societies.groups.Relation;
import org.societies.groups.setting.*;

/**
 * Represents a SettingModule
 */
public class SettingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SettingProvider.class).to(CollectiveSettingProvider.class);

        bind(LocationSetting.ID, "home", new TypeLiteral<Setting<Location>>() {
        }, LocationSetting.class);
        bind(RelationSetting.ID, new TypeLiteral<Setting<Relation>>() {
        }, new RelationSetting());
        bindNamed(VerifiedSetting.ID, "verify", new TypeLiteral<Setting<Boolean>>() {
        }, new VerifiedSetting());
        bindNamed("personal-ff", new TypeLiteral<Setting<Boolean>>() {
        }, new BooleanSetting("personal-ff"));
        bindNamed("group-ff", new TypeLiteral<Setting<Boolean>>() {
        }, new BooleanSetting("group-ff"));
        bindNamed("balance", "group-balance", new TypeLiteral<Setting<Double>>() {
        }, new DoubleSetting("balance"));
    }

    public <T> void bindNamed(String id, TypeLiteral<Setting<T>> type, Setting<T> setting) {
        bindNamed(id, id, type, setting);
    }

    public <T> void bindNamed(String id, String internal, TypeLiteral<Setting<T>> type, Setting<T> setting) {
        bindNamed(internal, type).toInstance(setting);
        settings().addBinding(id).toInstance(setting);
    }

    public <T> void bind(String id, String name, TypeLiteral<Setting<T>> type, Class<? extends Setting<T>> setting) {
        bindNamed(name, type).to(setting);
        settings().addBinding(id).to(setting);
    }

    public <T> void bind(String id, TypeLiteral<Setting<T>> type, Setting<T> setting) {
        bind(type).toInstance(setting);
        settings().addBinding(id).toInstance(setting);
    }

    private MapBinder<String, Setting> settings() {
        return MapBinder.newMapBinder(binder(), String.class, Setting.class, Names.named("settings"));
    }

}
