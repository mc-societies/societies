package net.catharos.societies.commands.society.home;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home", async = true)
//todoCommands
public class HomeCommand implements Executor<Sender> {



    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {

    }
}
