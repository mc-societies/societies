package net.catharos.societies.commands.society.rank;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.RankPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a CreateCommand
 */
@Command(identifier = "command.rank.create")
public class CreateCommand implements Executor<SocietyMember> {

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
