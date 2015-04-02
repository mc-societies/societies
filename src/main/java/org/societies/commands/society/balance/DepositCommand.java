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

import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.deposit", async = true)
@Permission("societies.deposit")
@Sender(Member.class)
public class DepositCommand implements Executor<Member> {

    private final Locker locker;

    @Argument(name = "argument.deposit")
    double deposit;

    static final ReentrantLock lock = new ReentrantLock();

    @Inject
    public DepositCommand(Locker locker) {
        this.locker = locker;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        lock.lock();
        try {
            if (!locker.lock(0)) return;

            EconomyParticipant economy = sender.get(EconomyParticipant.class);

            EconomyResponse response = economy.withdraw(deposit);

            if (!response.transactionSuccess()) {
                sender.send("deposit-failed");
                return;
            }

            Society society = group.get(Society.class);

            double balance = society.getBalance();
            society.setBalance(balance + response.getAmount());

            sender.send("deposit-successfully", response.getAmount());

        } finally {
            unlock();
            lock.unlock();
        }
    }

    private void unlock() {
        locker.unlock(0);
    }
}
