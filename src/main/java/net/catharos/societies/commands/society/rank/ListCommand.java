package net.catharos.societies.commands.society.rank;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;

import java.util.Collection;

/**
 * Represents a ListCommand
 */
@Command(identifier = "command.rank.list")
public class ListCommand implements Executor<Member> {

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
