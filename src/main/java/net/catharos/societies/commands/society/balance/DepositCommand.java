package net.catharos.societies.commands.society.balance;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.societies.api.member.SocietyMember;
import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.deposit")
@Permission("societies.deposit")
@Sender(Member.class)
public class DepositCommand implements Executor<Member> {

    @Argument(name = "argument.deposit")
    double deposit;

    private final Setting<Double> balanceSetting;

    @Inject
    public DepositCommand(@Named("group-balance") Setting<Double> balanceSetting) {
        this.balanceSetting = balanceSetting;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        SocietyMember societyMember = (SocietyMember) sender;

        EconomyResponse response = societyMember.withdraw(deposit);

        if (!response.transactionSuccess()) {
            sender.send("deposit-failed");
            return;
        }

        double balance = group.getDouble(balanceSetting);
        group.set(balanceSetting, balance + response.amount);

        sender.send("deposit-successfully", response.amount);
    }
}
