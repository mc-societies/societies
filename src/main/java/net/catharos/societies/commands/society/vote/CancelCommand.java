package net.catharos.societies.commands.society.vote;

import net.catharos.groups.Member;
import net.catharos.groups.request.Request;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vote.cancel")
@Permission("societies.vote.cancel")
@Sender(Member.class)
public class CancelCommand implements Executor<Member> {

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Request supplied = sender.getSuppliedRequest();

        if (supplied == null) {
            sender.send("request.none-supplied");
            return;
        }

        supplied.cancel();
        sender.send("request.cancelled-by-you");
    }
}
