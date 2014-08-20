package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.ChatColor;


/**
 * Represents a PreCommandStep
 */
class PreCommandStep implements Executor<Sender> {


    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        sender.send(ChatColor.GRAY + ctx.getCommand().getName() + ChatColor.DARK_GRAY + "  -------------------------------");
    }
}
