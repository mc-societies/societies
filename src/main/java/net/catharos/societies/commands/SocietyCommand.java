package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a ClanCommand
 */
@Command(identifier = "society", description = "A default description!")
@Children(children = SocietyCommand.CreateCommand.class)
public class SocietyCommand {


    @Command(identifier = "create", description = "A default description!")
    public static class CreateCommand implements Executor<Sender> {

        @Override
        public void execute(CommandContext<Sender> ctx, Sender sender) {
            sender.send("Fuck off!");
        }
    }
}
