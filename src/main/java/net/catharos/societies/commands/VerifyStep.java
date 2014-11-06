package net.catharos.societies.commands;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a RuleStep
 */
public class VerifyStep implements Executor<Sender> {

    public static final String VERIFY = "verify";

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if (!(sender instanceof Member)) {
            return;
        }

        Group group = ((Member) sender).getGroup();

        if (group == null) {
            return;
        }

        String verify = ctx.getCommand().get(VERIFY);

        if (verify != null && !group.isVerified()) {
            sender.send("not-verified");
            ctx.cancel();
        }
    }
}
