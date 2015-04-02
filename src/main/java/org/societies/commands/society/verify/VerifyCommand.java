package org.societies.commands.society.verify;

import order.CommandContext;
import order.Executor;
import order.reflect.Argument;
import order.reflect.Command;
import order.reflect.Permission;
import order.sender.Sender;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.verify", async = false)
@Permission("societies.verify")
public class VerifyCommand implements Executor<Sender> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        Society society = target.get(Society.class);
        society.setVerified(true);
        sender.send("target-society.verified", target.getTag());
    }
}
