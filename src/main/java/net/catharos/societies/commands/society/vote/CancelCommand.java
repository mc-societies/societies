package net.catharos.societies.commands.society.vote;

import net.catharos.groups.request.Request;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vote.cancel")
public class CancelCommand implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Request supplied = sender.getSuppliedRequest();

        if (supplied == null) {
            sender.send("request.none-supplied");
            return;
        }

        supplied.cancel();
        sender.send("request.cancelled-by-you");
    }
}
