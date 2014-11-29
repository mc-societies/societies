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
import net.catharos.societies.api.economy.EconomyParticipant;
import net.catharos.societies.api.lock.Locker;
import net.milkbowl.vault.economy.EconomyResponse;

import java.util.concurrent.ExecutionException;
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

    private final Setting<Double> balanceSetting;

    static final ReentrantLock lock = new ReentrantLock();

    @Inject
    public DepositCommand(Locker locker, @Named("group-balance") Setting<Double> balanceSetting) {
        this.locker = locker;
        this.balanceSetting = balanceSetting;
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
            if (!locker.lock(0).get()) return;

            EconomyParticipant economy = sender.get(EconomyParticipant.class);

            EconomyResponse response = economy.withdraw(deposit);

            if (!response.transactionSuccess()) {
                sender.send("deposit-failed");
                return;
            }

            double balance = group.getDouble(balanceSetting);
            group.set(balanceSetting, balance + response.amount);

            sender.send("deposit-successfully", response.amount);

        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        } finally {
            unlock();
            lock.unlock();
        }
    }

    private void unlock() {
        try {
            locker.unlock(0).get();
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }
    }
}
