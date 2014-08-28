package net.catharos.societies.commands.society.rank;

import net.catharos.groups.Group;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a RemoveCommand
 */
@Command(identifier = "command.rank.remove")
public class RemoveCommand  implements Executor<SocietyMember> {

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
