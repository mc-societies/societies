package org.societies.sieging.commands;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import net.catharos.lib.core.command.parser.ArgumentParser;
import org.shank.AbstractModule;
import org.societies.api.sieging.City;
import org.societies.sieging.commands.parser.CityParser;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.name.Names.named;

/**
 * Represents a SiegeCommandModule
 */
public class SiegeCommandModule extends AbstractModule {

    private final Class[] commands = {
            BindstoneCommand.class,

            SiegestoneCommand.class,
    };

    @Override
    protected void configure() {
        MapBinder<Class<?>, ArgumentParser<?>> parsers = MapBinder
                .newMapBinder(binder(), new TypeLiteral<Class<?>>() {}, new TypeLiteral<ArgumentParser<?>>() {}, Names
                        .named("parsers"));


        bind(new TypeLiteral<ArgumentParser<City>>() {}).to(CityParser.class);
        parsers.addBinding(City.class).to(CityParser.class);


        Multibinder<Class> cmds = newSetBinder(
                binder(),
                new TypeLiteral<Class>() {},
                named("custom-commands")
        );

        for (Class command : commands) {
            cmds.addBinding().toInstance(command);
        }
    }
}
