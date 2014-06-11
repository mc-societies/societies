package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a ThreadTestCommand
 */
@Command(identifier = "tt", async = false)
public class ThreadTestCommand implements Executor<Sender> {


    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        System.out.println(Thread.currentThread());
    }
}
