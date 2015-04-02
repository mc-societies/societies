package org.societies.commands;

import com.google.inject.Inject;
import order.CommandContext;
import order.CommandException;
import order.ExecuteException;
import order.ParsingException;
import order.sender.Sender;
import org.apache.logging.log4j.Logger;

/**
 * Represents a CommandExceptionHandler
 */
class CommandExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Logger logger;

    @Inject
    CommandExceptionHandler(Logger logger) {
        this.logger = logger;
    }

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
