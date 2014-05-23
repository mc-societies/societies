package net.catharos.societies.commands;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Commands;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.reflect.instance.ReflectionFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.core.command.token.Delimiter;
import net.catharos.lib.core.command.token.SpaceDelimiter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a CommandModule
 */
public class CommandModule extends net.catharos.lib.shank.AbstractModule {

    @Override
    protected void configure() {
        install(new DefaultParserModule());

        install(new FactoryModuleBuilder()
                .build(new TypeLiteral<ReflectionFactory<Sender>>() {}));

        bindNamed("help-executor", new TypeLiteral<Executor<Sender>>() {}).toInstance(new Executor<Sender>() {
            @Override
            public void execute(CommandContext<Sender> ctx, Sender sender) {

            }
        });

        bind(Delimiter.class).to(SpaceDelimiter.class);

        bind(SenderProvider.class).toInstance(new SenderProvider() {
            @Nullable
            @Override
            public Sender getSender(String name) {
                return null;
            }

            @Nullable
            @Override
            public Sender getSender(UUID uuid) {
                return null;
            }
        });

        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);
    }

    @Provides
    @Named("global-command")
    public Commands<Sender> provideCommand(CommandAnalyser<Sender> analyser, Commands<Sender> commands) {
        commands.addChild(analyser.analyse(SocietyCommand.class));
        return commands;
    }
}
