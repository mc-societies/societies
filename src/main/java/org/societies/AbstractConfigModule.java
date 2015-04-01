package org.societies;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import net.catharos.lib.core.util.CastSafe;
import org.shank.AbstractModule;
import org.shank.config.Settings;

/**
 * Represents a AbstractConfigModule
 */
public abstract class AbstractConfigModule extends AbstractModule {
    protected final Config config;

    public AbstractConfigModule(Config config) {
        this.config = config;
    }

    public void bindNamed(String key, String setting, Class clazz) {
        bind(Key.get(clazz, Names.named(key)), setting);
    }

    public void bind(Key key, String setting) {
        bind(CastSafe.<Key<Object>>toGeneric(key)).toInstance(config.getAnyRef(setting));
    }

    public void bind(String key, String setting, Class clazz) {
        bind(key(key, clazz), setting);
    }

    protected <T> Key<T> key(String key, Class<T> clazz) {
        return Key.get(clazz, Settings.create(key));
    }

    public void bind(String key, String setting, TypeLiteral clazz) {
        bind(Key.get(clazz, Settings.create(key)), setting);
    }
}
