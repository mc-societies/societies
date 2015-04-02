package org.societies.commands;

import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.sender.Sender;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

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

        Society society = group.get(Society.class);

        if (verify != null && !society.isVerified()) {
            sender.send("not-verified");
            ctx.cancel();
        }
    }
}
