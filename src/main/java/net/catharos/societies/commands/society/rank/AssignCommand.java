package net.catharos.societies.commands.society.rank;

import net.catharos.groups.Group;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a AssignCommand
 */
@Command(identifier = "command.rank.assign")
public class AssignCommand  implements Executor<SocietyMember> {

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
