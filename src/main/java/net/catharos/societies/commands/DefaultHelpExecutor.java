package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;

/**
* Represents a DefaultHelpExecutor
*/
class DefaultHelpExecutor implements Executor<Sender> {

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        System.out.println(ctx.getCommand());
    }
}
