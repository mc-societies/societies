package net.catharos.societies.commands.society.rank;

import com.google.inject.Inject;
import net.catharos.groups.Group;
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
@Command(identifier = "create", description = "A default description!")
public class CreateCommand implements Executor<SocietyMember> {

    @Argument(name = "name", description = "The name of the new rank")
    String name;

    @Option(name = "priority", description = "")
    int priority = Rank.DEFAULT_PRIORITY;

    private final RankFactory rankFactory;

    @Inject
    public CreateCommand(RankFactory rankFactory) {
        this.rankFactory = rankFactory;
    }

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("No society");
            return;
        }

        Rank rank = rankFactory.create(name, priority);
        group.addRank(rank);

        sender.send("Rank %s created!", name);
    }
}
