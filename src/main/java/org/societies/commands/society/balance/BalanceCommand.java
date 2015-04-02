package org.societies.commands.society.balance;

import order.CommandContext;
import order.Executor;
import order.reflect.Command;
import order.reflect.Permission;
import order.reflect.Sender;
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
