package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.CommandException;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a CommandExceptionHandler
 */
class CommandExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!(e instanceof CommandException)) {
            return;
        }

        CommandContext context = ((CommandException) e).getContext();

        Sender sender = context.getSender();


        if (e instanceof ParsingException) {
            sender.send("Failed parsing: " + e.getMessage());
        }  else if (e instanceof ExecuteException) {
            sender.send("Execution failed: " + e.getMessage());
        }

    }
}
