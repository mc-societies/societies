package net.catharos.societies.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.SystemSender;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.commands.CommandModule;

/**
 * Represents a SocietiesMain
 */
public class SocietiesMain {

    public static void main(String[] args) throws ParsingException {
        Injector injector = Guice.createInjector(/*new SocietiesModule(),*/ new CommandModule());

//        Group group = injector.getInstance(DefaultGroup.class);
//
//        injector.getInstance(SocietiesQueries.class);

        injector.getInstance(Key.get(new TypeLiteral<CommandAnalyser<Sender>>(){}));

        Commands<Sender> instance = injector
                .getInstance(Key.get(new TypeLiteral<Commands<Sender>>() {}, Names.named("global-command")));

        instance.parse(new SystemSender(), "society create").execute();
    }
}
