package org.societies.commands.society.balance;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.api.economy.EconomyParticipant;
import org.societies.api.economy.EconomyResponse;
import org.societies.api.lock.Locker;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.setting.Setting;

import java.util.concurrent.ExecutionException;

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
    private final Setting<Double> balanceSetting;

    @Inject
    public WithdrawCommand(Locker locker, @Named("group-balance") Setting<Double> balanceSetting) {
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

        //beautify
        DepositCommand.lock.lock();
        try {
            if (!locker.lock(0).get()) return;

            EconomyParticipant economy = sender.get(EconomyParticipant.class);

            double balance = group.getDouble(balanceSetting);

            if (balance < withdraw) {
                sender.send("withdraw-failed");
                return;
            }

            group.set(balanceSetting, balance - withdraw);


            EconomyResponse response = economy.deposit(withdraw);

            if (!response.transactionSuccess()) {
                sender.send("withdraw-failed");
                return;
            }

            sender.send("withdraw-successfully", withdraw);


        } catch (InterruptedException ignored) {
            return;
        } catch (ExecutionException ignored) {
            return;
        } finally {
            unlock();
            DepositCommand.lock.unlock();
        }
    }

    private void unlock() {
        try {
            locker.unlock(0).get();
        } catch (InterruptedException ignored) {
            return;
        } catch (ExecutionException ignored) {
            return;
        }
    }
}
