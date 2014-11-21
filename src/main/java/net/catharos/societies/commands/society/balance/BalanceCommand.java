package net.catharos.societies.commands.society.balance;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.balance")
@Permission("societies.balance")
@Sender(Member.class)
public class BalanceCommand implements Executor<Member> {

    private final Setting<Double> balanceSetting;

    @Inject
    public BalanceCommand(@Named("group-balance") Setting<Double> balanceSetting) {
        this.balanceSetting = balanceSetting;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        double balance = group.getDouble(balanceSetting);


        sender.send("balance-current", balance);
    }
}
