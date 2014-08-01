package net.catharos.societies.commands;

import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a DefaultHelpExecutor
 */
class GroupHelpExecutor<S extends Sender> implements Executor<S> {

    @Override
    public void execute(CommandContext<S> ctx, S sender) {
        GroupCommand<S> command = (GroupCommand<S>) ctx.getCommand();


        for (Command<S> cmd : command.getChildren()) {
            if (cmd instanceof ExecutableCommand) {
                ExecutableCommand executableCommand = (ExecutableCommand) cmd;

                sender.send(cmd.getIdentifier() + " " + executableCommand.getArgumentsAmount());
            } else {
                sender.send(cmd.getIdentifier());
            }
        }
    }
}
