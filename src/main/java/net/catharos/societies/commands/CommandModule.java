package net.catharos.societies.commands;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.catharos.groups.Group;
import net.catharos.groups.command.GroupParser;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.parser.ArgumentParser;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.reflect.instance.ReflectionFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.token.Delimiter;
import net.catharos.lib.core.command.token.SpaceDelimiter;
import net.catharos.societies.commands.society.SocietyCommand;

/**
 * Represents a CommandModule
 */
public class CommandModule extends net.catharos.lib.shank.AbstractModule {

    @Override
    protected void configure() {
        install(new DefaultParserModule());

        install(new FactoryModuleBuilder()
                .build(new TypeLiteral<ReflectionFactory<Sender>>() {}));

        bindNamed("help-executor", new TypeLiteral<Executor<Sender>>() {}).to(new TypeLiteral<DefaultHelpExecutor<Sender>>() {});

        bind(Delimiter.class).to(SpaceDelimiter.class);

        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);


        MapBinder<Class<?>, ArgumentParser<?>> parsers = MapBinder
                .newMapBinder(binder(), new TypeLiteral<Class<?>>() {}, new TypeLiteral<ArgumentParser<?>>() {}, Names
                        .named("parsers"));

        bind(new TypeLiteral<ArgumentParser<Group>>() {}).to(GroupParser.class);
        parsers.addBinding(Group.class).to(GroupParser.class);
    }

    @Provides
    @Named("global-command")
    public Commands<Sender> provideCommand(CommandAnalyser<Sender> analyser, Commands<Sender> commands) {
        commands.addChild(analyser.analyse(SocietyCommand.class));
        return commands;
    }

}
