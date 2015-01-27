package org.societies.sieging.commands;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import net.catharos.lib.core.command.Executor;
import org.shank.AbstractModule;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.name.Names.named;

/**
 * Represents a SiegeCommandModule
 */
public class SiegeCommandModule extends AbstractModule {

    private final Class[] commands = {
            BindstoneCommand.CreateCommand.class,
            BindstoneCommand.ListCommand.class,
            BindstoneCommand.RemoveCommand.class,
            BindstoneCommand.MoveLand.class,

            SiegeCommand.StartCommand.class,
            SiegeCommand.EndCommand.class,
            SiegeCommand.ListCommand.class,
    };

    @Override
    protected void configure() {
        Multibinder<Class<Executor>> cmds = newSetBinder(
                binder(),
                new TypeLiteral<Class<Executor>>() {},
                named("custom-commands")
        );

        for (Class command : commands) {
            cmds.addBinding().toInstance(command);
        }
    }
}
