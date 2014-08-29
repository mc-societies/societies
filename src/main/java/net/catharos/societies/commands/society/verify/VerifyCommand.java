package net.catharos.societies.commands.society.verify;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.verify")
public class VerifyCommand implements Executor<Sender> {

    @Argument(name = "argument.society.target")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        target.setState(0);
    }
}
