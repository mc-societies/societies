package org.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.ChatColor;

/**
 * Represents a PreCommandStep
 */
class HeaderExecutor implements Executor<Sender> {

    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        String message = GRAY + ctx.getCommand()
                .getName() + DARK_GRAY + "  -------------------------------";

        ctx.put("pre-length", message.length() - GRAY.length() - DARK_GRAY.length());
        sender.send(message);
    }
}
