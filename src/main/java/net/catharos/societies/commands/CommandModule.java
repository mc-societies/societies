package net.catharos.societies.commands;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.reflect.instance.ReflectionFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.societies.member.LoadingMemberCache;

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

        bind(SenderProvider.class).to(LoadingMemberCache.class);

        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);

        Multibinder<Executor<Sender>> commands = Multibinder
                .newSetBinder(binder(), new TypeLiteral<Executor<Sender>>() {});
//        commands.addBinding().to(ListSocietiesCommand.class);
    }
}
