package org.societies.commands.society.verify;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.groups.group.Group;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.verify", async = true)
@Permission("societies.verify")
public class VerifyCommand implements Executor<Sender> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        target.verify(true);
        sender.send("target-society.verified", target.getTag());
    }
}