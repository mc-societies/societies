package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.CommandException;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.shank.logging.InjectLogger;
import org.apache.logging.log4j.Logger;

/**
 * Represents a CommandExceptionHandler
 */
class CommandExceptionHandler implements Thread.UncaughtExceptionHandler {

    @InjectLogger
    private Logger logger;

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!(e instanceof CommandException)) {
            logger.catching(e);
            return;
        }

        CommandContext context = ((CommandException) e).getContext();

        Sender sender = context.getSender();


        if (e instanceof ParsingException) {
            sender.send(e.getMessage());
        } else if (e instanceof ExecuteException) {
            sender.send("Execution failed: " + e.getMessage());
        }

        if (e.getCause() != null) {
            logger.catching(e);
        }
    }
}
