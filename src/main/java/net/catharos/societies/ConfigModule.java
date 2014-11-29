package net.catharos.societies;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.lib.shank.config.Settings;

import java.util.List;

/**
 * Represents a ConfigModule
 */
public class ConfigModule extends AbstractModule {

    private final Config config;

    public ConfigModule(Config config) {this.config = config;}

    @Override
    protected void configure() {
        bind("name.max-length", "name.max-length", int.class);
        bind("name.min-length", "name.min-length", int.class);
        bind("name.disallowed", "name.disallowed", new TypeLiteral<List<String>>() {});


        Class<Integer> clazz = int.class;
        bind("tag.max-length", "tag.max-length", clazz);
        bind("tag.min-length", "tag.min-length", int.class);
        bind("tag.disallowed", "tag.disallowed", new TypeLiteral<List<String>>() {});

        bind("teleport.drop-items", "teleport.drop-items", boolean.class);
        bind("teleport.delay", "teleport.delay", int.class);
        bind("teleport.item-blacklist", "teleport.item-blacklist", new TypeLiteral<List<String>>() {});
        bind("teleport.item-whitelist", "teleport.item-whitelist", new TypeLiteral<List<String>>() {});

        bindNamed("database.mysql.database", "database.mysql.database", String.class);
        bindNamed("database.mysql.host", "database.mysql.host", String.class);
        bindNamed("database.mysql.port", "database.mysql.port", int.class);
        bindNamed("database.mysql.password", "database.mysql.password", String.class);
        bindNamed("database.mysql.username", "database.mysql.username", String.class);

        bind("blacklisted-worlds", "blacklisted-worlds", new TypeLiteral<List<String>>() {});

        bindNamed("entries-per-page", "chat.tables.max-rows-pre-page", int.class);

        bind("chat.integration", "chat.integration", boolean.class);

        bind("pvp.global-ff-forced", "pvp.global-ff-forced", boolean.class);
        bind("pvp.save-civilians", "pvp.save-civilians", boolean.class);

        bind("society.home.replace-spawn", "society.home.replace-spawn", boolean.class);
    }

    public void bindNamed(String key, String setting, Class clazz) {
        bind(Key.get(clazz, Names.named(key))).toInstance(config.getAnyRef(setting));
    }

    public void bind(String key, String setting, Class clazz) {
        bind(Key.get(clazz, Settings.create(key))).toInstance(config.getAnyRef(setting));
    }

    public void bind(String key, String setting, TypeLiteral clazz) {
        bind(Key.get(clazz, Settings.create(key))).toInstance(config.getAnyRef(setting));
    }
}
