package org.societies.commands;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import order.*;
import order.builder.GroupBuilder;
import order.parser.ArgumentParser;
import order.parser.DefaultParserModule;
import order.parser.IntegerParser;
import order.parser.TargetParser;
import order.reflect.instance.CommandAnalyser;
import order.reflect.instance.factory.InjectorInstanceFactory;
import order.reflect.instance.factory.InstanceFactory;
import order.sender.Sender;
import order.step.PermissionStep;
import order.token.Delimiter;
import order.token.DelimiterTokenizer;
import order.token.SpaceDelimiter;
import order.token.Tokenizer;
import org.shank.AbstractModule;
import org.societies.LocationParser;
import org.societies.SocietiesModule;
import org.societies.api.math.Location;
import org.societies.commands.society.*;
import org.societies.commands.society.balance.BalanceCommand;
import org.societies.commands.society.balance.DepositCommand;
import org.societies.commands.society.balance.WithdrawCommand;
import org.societies.commands.society.home.HomeCommand;
import org.societies.commands.society.promote.DemoteCommand;
import org.societies.commands.society.promote.PromoteCommand;
import org.societies.commands.society.rank.RankCommand;
import org.societies.commands.society.relation.AlliesCommand;
import org.societies.commands.society.relation.RivalsCommand;
import org.societies.commands.society.trust.DistrustCommand;
import org.societies.commands.society.trust.TrustCommand;
import org.societies.commands.society.verify.DisproveCommand;
import org.societies.commands.society.verify.VerifyCommand;
import org.societies.commands.society.vote.AbstainCommand;
import org.societies.commands.society.vote.AcceptCommand;
import org.societies.commands.society.vote.CancelCommand;
import org.societies.commands.society.vote.DenyCommand;
import org.societies.groups.command.GroupParser;
import org.societies.groups.command.MemberParser;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import java.util.Collections;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;
import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.name.Names.named;

/**
 * Represents a CommandModule
 */
public class CommandModule extends AbstractModule {

    public static final TypeLiteral<DefaultCommandPipeline<Sender>> PIPELINE_IMPL = new TypeLiteral<DefaultCommandPipeline<Sender>>() {
    };

    private final Class[] commands = new Class[]{
            CreateCommand.class,
            ListCommand.class,

            ProfileCommand.class,
            LookupCommand.class,

            JoinCommand.class,
            InviteCommand.class,

            CoordsCommand.class,
            VitalsCommand.class,
            RosterCommand.class,

            TrustCommand.class,
            DistrustCommand.class,
            PromoteCommand.class,
            DemoteCommand.class,

            AlliancesCommand.class,
            RivalriesCommand.class,

            FFCommand.class,
            GroupFFCommand.class,
            TagCommand.class,
            KickCommand.class,
            LeaveCommand.class,

            AlliesCommand.class,
            RivalsCommand.class,

            HomeCommand.class,
            RankCommand.class,

            BalanceCommand.class,
            DepositCommand.class,
            WithdrawCommand.class,

            AcceptCommand.class,
            DenyCommand.class,
            AbstainCommand.class,
            CancelCommand.class,

            VerifyCommand.class,
            DisproveCommand.class,

            ReloadCommand.class,
            BackupCommand.class,
            DebugCommand.class
    };

    private static final TypeLiteral<Executor<Sender>> EXECUTOR_TYPE = new TypeLiteral<Executor<Sender>>() {};

    @Override
    protected void configure() {
        bindNamedInstance("root-name", String.class, "Root");
        bindNamedInstance("root-description", String.class, "Root");

        // Parsers
        install(new DefaultParserModule());

        // Group parser
        bind(new TypeLiteral<ArgumentParser<Group>>() {}).to(GroupParser.class);
        parsers().addBinding(Group.class).to(GroupParser.class);
        parsers().addBinding(Location.class).to(LocationParser.class);

        // Member parser
        parsers().addBinding(Sender.class).to(TargetParser.class);
        parsers().addBinding(Member.class).to(new TypeLiteral<MemberParser>() {});


        // Exception handler
        bindNamed("command-exception-handler", Thread.UncaughtExceptionHandler.class).to(CommandExceptionHandler.class);

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
        bindNamedInstance("sync-executor", ListeningExecutorService.class, listeningDecorator(newDirectExecutorService()));
        bindNamed("async-executor", ListeningExecutorService.class).to(SocietiesModule.WORKER_EXECUTOR);

        bindNamed("provided", new TypeLiteral<CommandPipeline<Sender>>() {
        }).to(PIPELINE_IMPL);

        bindNamed("system-sender", Sender.class).to(SystemSender.class);

        newSetBinder(
                binder(),
                new TypeLiteral<Class>() {},
                named("custom-commands")
        );
    }


    @Provides
    @Named("help-pipeline")
    @Singleton
    public CommandPipeline<Sender> provideHelpPipeline(@Named("provided") CommandPipeline<Sender> pipeline,
                                                       HeaderExecutor header,
                                                       FooterExecutor footer) {
        pipeline.addBefore(header);
        pipeline.addAfter(footer);
        return pipeline;
    }

    @Provides
    @Singleton
    public CommandPipeline<Sender> providePipeline(@Named("provided") CommandPipeline<Sender> pipeline,
                                                   PermissionStep perm,
                                                   VerifyStep verify,
                                                   RuleStep rule,
                                                   HeaderExecutor header,
                                                   WorldStep world,
                                                   FooterExecutor footer) {
        pipeline.addBefore(perm);
        pipeline.addBefore(verify);
        pipeline.addBefore(rule);
        pipeline.addBefore(header);
        pipeline.addBefore(world);
        pipeline.addAfter(footer);
        return pipeline;
    }

    @Provides
    @Named("commands")
    public Set<Command<Sender>> provideCommand(GroupBuilder<Sender> builder, @Named("custom-commands") Set<Class> classes, CommandAnalyser<Sender> analyser) {

        GroupCommand<Sender> group = builder.identifier("s").name("Societies").build();

        group.addOption(new Argument<Integer>("page", "page", "page", true, new IntegerParser()));

        for (Class<?> clazz : this.commands) {
            group.addChild(analyser.analyse(clazz));
        }

        for (Class<?> clazz : classes) {
            group.addChild(analyser.analyse(clazz));
        }

        return Collections.<Command<Sender>>singleton(group);
    }

    public MapBinder<Class<?>, ArgumentParser<?>> parsers() {
        return newMapBinder(binder(),
                new TypeLiteral<Class<?>>() {},
                new TypeLiteral<ArgumentParser<?>>() {},
                named("parsers")
        );
    }

    public Multibinder<Executor<Sender>> afterPipeline(Binder binder) {
        return newSetBinder(binder, new TypeLiteral<Executor<Sender>>() {}, named("pipeline-after"))
                .permitDuplicates();
    }

    public Multibinder<Executor<Sender>> beforePipeline(Binder binder) {
        return newSetBinder(binder, new TypeLiteral<Executor<Sender>>() {}, named("pipeline-before"))
                .permitDuplicates();
    }
}
