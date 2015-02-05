package org.societies;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import net.catharos.lib.core.util.CastSafe;
import org.shank.AbstractModule;
import org.shank.config.Settings;

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

        bind("blacklisted-worlds", "blacklisted-worlds", new TypeLiteral<List<String>>() {});

        bindNamed("entries-per-page", "chat.tables.max-rows-pre-page", int.class);

        bind("chat.integration", "chat.integration", boolean.class);

        bind("pvp.global-ff-forced", "pvp.global-ff-forced", boolean.class);
        bind("pvp.save-civilians", "pvp.save-civilians", boolean.class);

        bind("home.replace-spawn", "society.home.replace-spawn", boolean.class);

        bind("verification-required", "verification.new-society-verification-required", boolean.class);

        bind("relations.min-size-to-set-rival", "relations.min-size-to-set-rival", int.class);
        bind("relations.unrivable-societies", "relations.unrivable-societies", new TypeLiteral<List<String>>() {});
        bind("relations.rival-limit-percent", "relations.rival-limit-percent", int.class);

        bind("relations.min-size-to-set-ally", "relations.min-size-to-set-ally", int.class);
        bind("trust.trust-members-by-default", "trust.trust-members-by-default", boolean.class);
        bind("society.max-size", "society.max-size", int.class);
        bind("verification.show-unverified", "verification.show-unverified", boolean.class);

        bindNamed("server-identity", "server-identity", short.class);
    }

    public void bindNamed(String key, String setting, Class clazz) {
        bind(Key.get(clazz, Names.named(key)), setting);
    }

    public void bind(Key key, String setting) {
        bind(CastSafe.<Key<Object>>toGeneric(key)).toInstance(config.getAnyRef(setting));
    }

    public void bind(String key, String setting, Class clazz) {
        bind(Key.get(clazz, Settings.create(key)), setting);
    }

    public void bind(String key, String setting, TypeLiteral clazz) {
        bind(Key.get(clazz, Settings.create(key)), setting);
    }
}
