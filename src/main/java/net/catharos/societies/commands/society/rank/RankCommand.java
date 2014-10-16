package net.catharos.societies.commands.society.rank;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.RankPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.ChatColor;

import javax.inject.Provider;
import java.util.Collection;

/**
 * Represents a RankCommand
 */
@Command(identifier = "command.rank.rank")
@Children({
        RankCommand.CreateCommand.class,
        RankCommand.RemoveCommand.class,
        RankCommand.ListCommand.class,
        RankCommand.AssignCommand.class
})
public class RankCommand {

    @Command(identifier = "command.rank.create")
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.create"))
    public static class CreateCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
        String name;

        @Option(name = "argument.rank.priority")
        int priority = Rank.DEFAULT_PRIORITY;

        private final RankFactory rankFactory;
        private final RankPublisher rankPublisher;

        @Inject
        public CreateCommand(RankFactory rankFactory, RankPublisher rankPublisher) {
            this.rankFactory = rankFactory;
            this.rankPublisher = rankPublisher;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            name = ChatColor.stripColor(name);

            Rank rank = rankFactory.create(name, priority);


            if (group.getRank(name) != null) {
                sender.send("rank.already-exists");
                return;
            }

            this.rankPublisher.publishRank(group, rank);
            group.addRank(rank);

            sender.send("rank.created", name);
        }
    }


    @Command(identifier = "command.rank.remove")
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.remove"))
    public static class RemoveCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
        String name;

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Rank rank = group.getRank(name);

            if (rank == null) {
                sender.send("rank.not-found");
                return;
            }

            group.removeRank(rank);

            sender.send("rank.removed", name);
        }
    }

    @Command(identifier = "command.rank.list")
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.list"))
    public static class ListCommand implements Executor<Member> {

        private final Provider<Table> tableProvider;

        @Option(name = "argument.page")
        int page;

        @Inject
        public ListCommand(Provider<Table> tableProvider) {
            this.tableProvider = tableProvider;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }


            Collection<Rank> ranks = group.getRanks();

            if (ranks.isEmpty()) {
                sender.send("ranks.not-found");
                return;
            }

            Table table = tableProvider.get();

            for (Rank rank : ranks) {
                table.addForwardingRow(rank);
            }

            sender.send(table.render(ctx.getName(), page));
        }
    }


    @Command(identifier = "command.rank.assign")
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.assign"))
    public static class AssignCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.rank")
        String rankName;

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Rank rank = group.getRank(rankName);

            if (rank == null) {
                sender.send("rank.not-found");
                return;
            }

            sender.addRank(rank);

            sender.send("rank.assigned", rank.getName(), sender.getName());
        }
    }
}
