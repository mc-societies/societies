package org.societies.commands.society.balance;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.api.economy.EconomyParticipant;
import org.societies.api.economy.EconomyResponse;
import org.societies.api.lock.Locker;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.withdraw", async = true)
@Permission("societies.withdraw")
@Sender(Member.class)
public class WithdrawCommand implements Executor<Member> {

    @Argument(name = "argument.withdraw")
    double withdraw;

    private final Locker locker;

    @Inject
    public WithdrawCommand(Locker locker) {
        this.locker = locker;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        //beautify
        DepositCommand.lock.lock();
        try {
            if (!locker.lock(0)) return;

            EconomyParticipant economy = sender.get(EconomyParticipant.class);
            Society society = group.get(Society.class);

            double balance = society.getBalance();

            if (balance < withdraw) {
                sender.send("withdraw-failed");
                return;
            }

            society.setBalance(balance - withdraw);


            EconomyResponse response = economy.deposit(withdraw);

            if (!response.transactionSuccess()) {
                sender.send("withdraw-failed");
                return;
            }

            sender.send("withdraw-successfully", withdraw);

        } finally {
            unlock();
            DepositCommand.lock.unlock();
        }
    }

    private void unlock() {
        locker.unlock(0);
    }
}
