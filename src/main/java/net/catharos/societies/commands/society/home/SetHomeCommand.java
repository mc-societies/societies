package net.catharos.societies.commands.society.home;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.Location;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.set", async = true)
//todoCommands
public class SetHomeCommand implements Executor<Sender> {

    @Argument(name = "argument.location")
    Location location;


    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {

    }
}
