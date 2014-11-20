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
@Command(identifier = "command.withdraw")
@Permission("societies.withdraw")
@Sender(Member.class)
public class WithdrawCommand implements Executor<Member> {

    @Argument(name = "argument.withdraw")
    double withdraw;

    private final Setting<Double> balanceSetting;

    @Inject
    public WithdrawCommand(@Named("group-balance") Setting<Double> balanceSetting) {
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

        double balance = group.getDouble(balanceSetting);

        if (balance < withdraw) {
            sender.send("withdraw-failed");
            return;
        }

        group.set(balanceSetting, balance - withdraw);


        EconomyResponse response = societyMember.deposit(withdraw);

        if (!response.transactionSuccess()) {
            sender.send("withdraw-failed");
            return;
        }

        sender.send("withdraw-successfully", withdraw);
    }
}
