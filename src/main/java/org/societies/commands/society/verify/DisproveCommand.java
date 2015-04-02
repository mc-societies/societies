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
@Command(identifier = "command.disprove", async = true)
@Permission("societies.disprove")
public class DisproveCommand implements Executor<Sender> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        Society society = target.get(Society.class);
        society.setVerified(false);
        sender.send("target-society.disproved", target.getTag());
    }
}
