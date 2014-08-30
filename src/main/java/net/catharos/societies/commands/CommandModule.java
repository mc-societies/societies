package net.catharos.societies.commands;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.groups.command.GroupParser;
import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.builder.GroupBuilder;
import net.catharos.lib.core.command.format.pagination.DefaultPaginator;
import net.catharos.lib.core.command.format.pagination.Paginator;
import net.catharos.lib.core.command.parser.ArgumentParser;
import net.catharos.lib.core.command.parser.DefaultParserModule;
import net.catharos.lib.core.command.parser.TargetParser;
import net.catharos.lib.core.command.reflect.instance.CommandAnalyser;
import net.catharos.lib.core.command.reflect.instance.factory.InjectorInstanceFactory;
import net.catharos.lib.core.command.reflect.instance.factory.InstanceFactory;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.step.PermissionStep;
import net.catharos.lib.core.command.token.Delimiter;
import net.catharos.lib.core.command.token.DelimiterTokenizer;
import net.catharos.lib.core.command.token.SpaceDelimiter;
import net.catharos.lib.core.command.token.Tokenizer;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.bukkit.BukkitSystemSender;
import net.catharos.societies.bukkit.LocationParser;
import net.catharos.societies.commands.society.*;
import net.catharos.societies.commands.society.home.HomeCommand;
import net.catharos.societies.commands.society.home.RemoveHomeCommand;
import net.catharos.societies.commands.society.home.SetHomeCommand;
import net.catharos.societies.commands.society.rank.RankCommand;
import net.catharos.societies.commands.society.relation.RelationCommand;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.Location;

import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.google.inject.name.Names.named;

/**
 * Represents a CommandModule
 */
public class CommandModule extends AbstractModule {


    private Class<?>[] commands = new Class<?>[]{
            CreateCommand.class,
            RenameCommand.class,
            ProfileCommand.class,
            RosterCommand.class,
            ListCommand.class,
            InviteCommand.class,

            JoinCommand.class,
            FastJoinCommand.class,
            LeaveCommand.class,

            AcceptCommand.class,
            DenyCommand.class,
            AbstainCommand.class,

            HomeCommand.class,
            SetHomeCommand.class,
            RemoveHomeCommand.class,

            RankCommand.class,
            RelationCommand.class,
            ThreadTestCommand.class
    };

    private static final TypeLiteral<Executor<Sender>> EXECUTOR_TYPE = new TypeLiteral<Executor<Sender>>() {};

    @Override
    protected void configure() {
        bindNamedInstance("root-name", String.class, "Root");
        bindNamedInstance("root-description", String.class, "Root");

        // Parsers
        install(new DefaultParserModule());

        // Paginator
        bindNamedInstance("table-header", String.class, "Societies {3} - page {0}/{1} - {2} entries");
        bindNamedInstance("padding", int.class, 2);
        bind(Paginator.class).to(DefaultPaginator.class);
        bindNamedInstance("entires-per-page", int.class, 9);

        // Group parser
        bind(new TypeLiteral<ArgumentParser<Group>>() {}).to(GroupParser.class);
        parsers().addBinding(Group.class).to(GroupParser.class);
        parsers().addBinding(Location.class).to(LocationParser.class);

        // Member parser
        //        bind(new TypeLiteral<ArgumentParser<SocietyMember>>() {}).to(TargetParser.class);
        parsers().addBinding(SocietyMember.class).to(TargetParser.class);


        // Exception handler
        bindNamedInstance("command-exception-handler", Thread.UncaughtExceptionHandler.class, new CommandExceptionHandler());

        // Help executor
        bindNamed("help-executor", EXECUTOR_TYPE)
                .toProvider(new TypeLiteral<PipelinedProvider<Sender, HelpExecutor<Sender>>>() {});
        bindNamed("group-help-executor", EXECUTOR_TYPE)
                .toProvider(new TypeLiteral<PipelinedProvider<Sender, GroupHelpExecutor<Sender>>>() {});

        // Tokenizer
        bind(Tokenizer.class).to(DelimiterTokenizer.class);
        bind(Delimiter.class).to(SpaceDelimiter.class);

        // Reflection api
        bind(InstanceFactory.class).to(InjectorInstanceFactory.class);

        // Sync/Async command executors
        bindNamedInstance("sync-executor", ListeningExecutorService.class, sameThreadExecutor());
        bindNamedInstance("async-executor", ListeningExecutorService.class, sameThreadExecutor()/*listeningDecorator(newFixedThreadPool(2))*/);


        bind(new TypeLiteral<CommandPipeline<Sender>>() {}).to(new TypeLiteral<DefaultCommandPipeline<Sender>>() {});
        beforePipeline().addBinding().to(new TypeLiteral<PermissionStep<Sender>>() {});
        beforePipeline().addBinding().to(PreCommandStep.class);

        bindNamed("system-sender", Sender.class).to(BukkitSystemSender.class);
    }

    @Provides
    @Named("commands")
    public Set<Command<Sender>> provideCommand(GroupBuilder<Sender> builder, CommandAnalyser<Sender> analyser) {
        Set<Command<Sender>> commands = new THashSet<Command<Sender>>();

        builder.name("Societies")
                .identifier("society")
                .description("Society command");

        GroupCommand<Sender> society = builder.build();

        for (Class<?> subCommand : this.commands) {
            society.addChild(analyser.analyse(subCommand));
        }

        commands.add(society);

        return commands;
    }

    public MapBinder<Class<?>, ArgumentParser<?>> parsers() {
        return newMapBinder(binder(),
                new TypeLiteral<Class<?>>() {},
                new TypeLiteral<ArgumentParser<?>>() {},
                named("parsers")
        );
    }

    public Multibinder<Executor<Sender>> afterPipeline() {
        return Multibinder
                .newSetBinder(binder(), new TypeLiteral<Executor<Sender>>() {}, Names.named("pipeline-after"));
    }

    public Multibinder<Executor<Sender>> beforePipeline() {
        return Multibinder
                .newSetBinder(binder(), new TypeLiteral<Executor<Sender>>() {}, Names.named("pipeline-before"))
                .permitDuplicates();
    }

}
