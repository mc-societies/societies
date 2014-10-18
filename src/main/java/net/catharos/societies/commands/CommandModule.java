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
import net.catharos.groups.Member;
import net.catharos.groups.command.GroupParser;
import net.catharos.groups.command.MemberParser;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.Command;
import net.catharos.lib.core.command.CommandPipeline;
import net.catharos.lib.core.command.DefaultCommandPipeline;
import net.catharos.lib.core.command.Executor;
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
import net.catharos.societies.SocietiesModule;
import net.catharos.societies.bukkit.BukkitSystemSender;
import net.catharos.societies.bukkit.LocationParser;
import net.catharos.societies.commands.society.SocietyCommand;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.setting.RulesSetting;
import org.bukkit.Location;

import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.google.inject.name.Names.named;

/**
 * Represents a CommandModule
 */
public class CommandModule extends AbstractModule {

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
//                bind(new TypeLiteral<ArgumentParser<SocietyMember>>() {}).to(TargetParser.class);
        parsers().addBinding(Sender.class).to(TargetParser.class);
        parsers().addBinding(Member.class).to(new TypeLiteral<MemberParser<SocietyMember>>() {});
        parsers().addBinding(SocietyMember.class).to(new TypeLiteral<MemberParser<SocietyMember>>() {});


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
        bindNamed("async-executor", ListeningExecutorService.class).to(SocietiesModule.WORKER_EXECUTOR);
//        bindNamedInstance("async-executor", ListeningExecutorService.class, sameThreadExecutor());


        bind(new TypeLiteral<CommandPipeline<Sender>>() {}).to(new TypeLiteral<DefaultCommandPipeline<Sender>>() {});
        beforePipeline().addBinding().to(new TypeLiteral<PermissionStep<Sender>>() {});
        beforePipeline().addBinding().to(RuleStep.class);
        beforePipeline().addBinding().to(HeaderExecutor.class);
        beforePipeline().addBinding().to(WorldStep.class);
        afterPipeline().addBinding().to(FooterExecutor.class);

        bindNamed("system-sender", Sender.class).to(BukkitSystemSender.class);

        addRule("create", 0x0);
        addRule("invite", 0x1);
        addRule("join", 0x2);
        addRule("leave", 0x3);
        addRule("vitals", 0x4);
        addRule("roster", 0x5);
        addRule("kick", 0x6);
        addRule("coords", 0x7);
        addRule("trust", 0x8);

        addRule("home.teleport", 0x10);
        addRule("home.regroup", 0x11);
        addRule("home.set", 0x12);

        addRule("rank.assign", 0x20);
        addRule("rank.create", 0x21);
        addRule("rank.list", 0x22);
        addRule("rank.remove", 0x23);

        addRule("rank.rules.assign", 0x30);
        addRule("rank.rules.list", 0x31);
        addRule("rank.rules.remove", 0x32);

        addRule("rivalries", 0x40);
        addRule("alliances", 0x41);

        addRule("allies.list", 0x50);
        addRule("allies.add", 0x51);
        addRule("allies.remove", 0x52);

        addRule("rivals.list", 0x60);
        addRule("rivals.add", 0x61);
        addRule("rivals.remove", 0x62);
    }

    private void addRule(String rule, int id) {
        rules().addBinding(rule).toInstance(new RulesSetting(rule, id));
    }

    @Provides
    @Named("commands")
    public Set<Command<Sender>> provideCommand(/*GroupBuilder<Sender> builder,*/ CommandAnalyser<Sender> analyser) {
        Set<Command<Sender>> commands = new THashSet<Command<Sender>>();

//        builder.name("Societies")
//                .identifier("society")
//                .description("Society command");
//
//        GroupCommand<Sender> society = builder.build();
//
//        for (Class<?> subCommand : this.commands) {
//            society.addChild(analyser.analyse(subCommand));
//        }
//
//        analyser.analyseOptions(society, builder.getExecutor().getClass());

        commands.add(analyser.analyse(SocietyCommand.class));

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
                .newSetBinder(binder(), new TypeLiteral<Executor<Sender>>() {}, Names.named("pipeline-after"))
                .permitDuplicates();
    }

    public Multibinder<Executor<Sender>> beforePipeline() {
        return Multibinder
                .newSetBinder(binder(), new TypeLiteral<Executor<Sender>>() {}, Names.named("pipeline-before"))
                .permitDuplicates();
    }

    public MapBinder<String, Setting> rules() {
        return MapBinder.newMapBinder(binder(), String.class, Setting.class);
    }


}
