package net.catharos.societies.commands;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.command.GroupParser;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.GroupCommand;
import net.catharos.lib.core.command.builder.GroupBuilder;
import net.catharos.lib.core.command.parser.ArgumentParser;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.parser.TargetParser;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.token.Delimiter;
import net.catharos.lib.core.command.token.DelimiterTokenizer;
import net.catharos.lib.core.command.token.SpaceDelimiter;
import net.catharos.lib.core.command.token.Tokenizer;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.commands.society.*;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;
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
public class CommandModule extends AbstractModule {

    public static final TypeLiteral<Executor<Sender>> EXECUTOR_TYPE = new TypeLiteral<Executor<Sender>>() {};

    @Override
    protected void configure() {
        bindNamedInstance("root-description", String.class, "Root");

        // Parsers
        install(new DefaultParserModule());

        // Group parser
        bind(new TypeLiteral<ArgumentParser<Group>>() {}).to(GroupParser.class);
        parsers().addBinding(Group.class).to(GroupParser.class);

        // Member parser
        //        bind(new TypeLiteral<ArgumentParser<SocietyMember>>() {}).to(TargetParser.class);
        parsers().addBinding(SocietyMember.class).to(TargetParser.class);


        // Exception handler
        bindNamedInstance("command-exception-handler", Thread.UncaughtExceptionHandler.class, new CommandExceptionHandler());

        // Help executor
        bindNamed("help-executor", EXECUTOR_TYPE)
                .to(new TypeLiteral<HelpExecutor<Sender>>() {});
        bindNamed("group-help-executor", EXECUTOR_TYPE)
                .to(new TypeLiteral<GroupHelpExecutor<Sender>>() {});

        // Tokenizer
        bind(Tokenizer.class).to(DelimiterTokenizer.class);
        bind(Delimiter.class).to(SpaceDelimiter.class);


        // Reflection api
        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);

        // Sync/Async command executors
        bindNamedInstance("sync-executor", ListeningExecutorService.class, sameThreadExecutor());
        bindNamedInstance("async-executor", ListeningExecutorService.class, listeningDecorator(newFixedThreadPool(2)));
    }

    @Provides
    @Named("commands")
    public Set<Command<Sender>> provideCommand(GroupBuilder<Sender> builder, CommandAnalyser<Sender> analyser) {
        Set<Command<Sender>> commands = new THashSet<Command<Sender>>();

        builder.identifier("society");
        builder.description("Society command");
        GroupCommand<Sender> society = builder.build();


        Class<?>[] subCommands = {
                CreateCommand.class,
//        AbandonCommand.class,
                ProfileCommand.class,
                ListCommand.class,
                InviteCommand.class,

                JoinCommand.class,
                LeaveCommand.class,

                AcceptCommand.class,
                DenyCommand.class,
                AbstainCommand.class

//        RankCommand.class,
//        RelationCommand.class
        };

        for (Class<?> subCommand : subCommands) {
            society.addChild(analyser.analyse(subCommand));
        }

        commands.add(society);

        commands.add(analyser.analyseExecutable(ThreadTestCommand.class));

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
