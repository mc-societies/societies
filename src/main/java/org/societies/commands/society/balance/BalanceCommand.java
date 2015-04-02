package org.societies.commands.society.balance;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.balance")
@Permission("societies.balance")
@Sender(Member.class)
public class BalanceCommand implements Executor<Member> {

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Society society = group.get(Society.class);

        double balance = society.getBalance();

        sender.send("balance-current", balance);
    }
}
