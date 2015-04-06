package org.societies.bukkit.converter;

import com.google.inject.PrivateModule;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.shank.AbstractModule;
import org.societies.api.converter.Converter;
import org.societies.converter.DummyConverter;

/**
 * Represents a ConverterModule
 */
public class ConverterModule extends AbstractModule {

    private final Server server;

    public ConverterModule(Server server) {
        this.server = server;
    }

    @Override
    protected void configure() {
        Plugin[] plugins = server.getPluginManager().getPlugins();

        for (final Plugin plugin : plugins) {

            try {
                if (plugin instanceof SimpleClans) {

                    install(new PrivateModule() {
                        @Override
                        protected void configure() {
                            bind(SimpleClans.class).toInstance((SimpleClans) plugin);
                            bind(Converter.class).to(SimpleClansConverter.class);
                            expose(Converter.class);
                        }
                    });

                    return;
                }
            } catch (NoClassDefFoundError ignored) {
                continue;
            }

            try {
                if (plugin instanceof com.p000ison.dev.simpleclans2.SimpleClans) {

                    install(new PrivateModule() {
                        @Override
                        protected void configure() {
                            bind(com.p000ison.dev.simpleclans2.SimpleClans.class)
                                    .toInstance((com.p000ison.dev.simpleclans2.SimpleClans) plugin);
                            bind(Converter.class).to(SimpleClans2Converter.class);
                            expose(Converter.class);
                        }
                    });

                    return;
                }
            } catch (NoClassDefFoundError ignored) {
                continue;
            }
        }

        bind(Converter.class).to(DummyConverter.class);
    }
}
