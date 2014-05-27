package net.catharos.societies.commands;

import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a DefaultHelpExecutor
 */
class DefaultHelpExecutor<S extends Sender> implements Executor<S> {

    private void displayHelp(S sender, GroupCommand<S> command) {
        for (Command<S> child : command.getChildren()) {
            displayHelp(sender, child);
        }
    }

    private void displayHelp(S sender, ExecutableCommand<S> command) {
        sender.send(command.getIdentifier());
    }

    @Override
    public void execute(CommandContext<S> ctx, S sender) {
        Command<S> command = ctx.getCommand();
        displayHelp(sender, command);
    }

    private void displayHelp(S sender, Command<S> command) {
        if (command instanceof GroupCommand) {
            displayHelp(sender, (GroupCommand<S>) command);
        } else {
            displayHelp(sender, (ExecutableCommand<S>) command);
        }
    }
}
