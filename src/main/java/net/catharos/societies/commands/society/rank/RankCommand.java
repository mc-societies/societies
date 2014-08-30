package net.catharos.societies.commands.society.rank;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.RankPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.member.SocietyMember;

import java.util.Collection;

/**
 * Represents a RankCommand
 */
@Command(identifier = "command.rank")
@Children(children = {
        RankCommand.CreateCommand.class,
        RankCommand.RemoveCommand.class,
        RankCommand.ListCommand.class,
        RankCommand.AssignCommand.class
})
public class RankCommand {

    @Command(identifier = "command.rank.create")
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
                sender.send("society.not.found");
                return;
            }

            Rank rank = rankFactory.create(name, priority);
            this.rankPublisher.publish(rank);
            group.addRank(rank);

            sender.send("rank.created", name);
        }
    }


    @Command(identifier = "command.rank.remove")
    public static class RemoveCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.rank.name", description = "The name of the new rank")
        String name;

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not.found");
                return;
            }

            Rank rank = group.getRank(name);

            if (rank == null) {
                sender.send("rank.not.found");
                return;
            }

            group.removeRank(rank);

            sender.send("rank.removed", name);
        }
    }

    @Command(identifier = "command.rank.list")
    public static class ListCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not.found");
                return;
            }


            Collection<Rank> ranks = group.getRanks();


            for (Rank rank : ranks) {
                sender.send(rank.getName());
            }
        }
    }


    @Command(identifier = "command.rank.assign")
    public static class AssignCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.rank")
        String rankName;

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not.found");
                return;
            }

            Rank rank = group.getRank(rankName);

            if (rank == null) {
                sender.send("rank.not.found");
                return;
            }

            sender.addRank(rank);

            sender.send("rank.assigned", rank.getName(), sender.getName());
        }
    }
}
