package net.catharos.societies.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.lib.core.command.reflect.instance.ReflectionFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.commands.ClanCommand;
import net.catharos.societies.commands.CommandModule;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new CommandModule());

        ReflectionFactory<Sender> instance = injector
                .getInstance(Key.get(new TypeLiteral<ReflectionFactory<Sender>>() {}));

        instance.create(ClanCommand.class);
    }
}
