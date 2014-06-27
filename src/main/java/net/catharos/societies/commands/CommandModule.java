package net.catharos.societies.commands;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.command.GroupParser;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.parser.ArgumentParser;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.parser.TargetParser;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.reflect.instance.ReflectionFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.token.Delimiter;
import net.catharos.lib.core.command.token.DelimiterTokenizer;
import net.catharos.lib.core.command.token.SpaceDelimiter;
import net.catharos.lib.core.command.token.Tokenizer;
import net.catharos.societies.commands.society.SocietyCommand;
import net.catharos.societies.member.SocietyMember;

import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.google.inject.name.Names.named;
import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Represents a CommandModule
 */
public class CommandModule extends net.catharos.lib.shank.AbstractModule {

    @Override
    protected void configure() {
        install(new DefaultParserModule());

        install(new FactoryModuleBuilder().build(new TypeLiteral<ReflectionFactory<Sender>>() {}));

        bindNamed("help-executor", new TypeLiteral<Executor<Sender>>() {})
                .to(new TypeLiteral<DefaultHelpExecutor<Sender>>() {});

        bind(Delimiter.class).to(SpaceDelimiter.class);

        bind(Tokenizer.class).to(DelimiterTokenizer.class);

        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);

        bind(new TypeLiteral<ArgumentParser<Group>>() {}).to(GroupParser.class);
        parsers().addBinding(Group.class).to(GroupParser.class);

//        bind(new TypeLiteral<ArgumentParser<SocietyMember>>() {}).to(TargetParser.class);
        parsers().addBinding(SocietyMember.class).to(TargetParser.class);

        bindNamedInstance("sync-executor", ListeningExecutorService.class, sameThreadExecutor());
        bindNamedInstance("async-executor", ListeningExecutorService.class, listeningDecorator(newFixedThreadPool(2)));
    }

    @Provides
    @Named("commands")
    public Set<Command<Sender>> provideCommand(CommandAnalyser<Sender> analyser) {
        Set<Command<Sender>> commands = new THashSet<Command<Sender>>();

        commands.add(analyser.analyse(SocietyCommand.class));
        commands.add(analyser.analyse(ThreadTestCommand.class));

        return commands;
    }

    public MapBinder<Class<?>, ArgumentParser<?>> parsers() {
        return newMapBinder(binder(),
                new TypeLiteral<Class<?>>() {},
                new TypeLiteral<ArgumentParser<?>>() {},
                named("parsers")
        );
    }
}
