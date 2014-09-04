package net.catharos.societies.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;


/**
 * Represents a PreCommandStep
 */
class FooterExecutor implements Executor<Sender> {

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        Integer length = ctx.get("pre-length");

        if (length == null) {
            return;
        }

        sender.send(ChatColor.DARK_GRAY + StringUtils.repeat('-', length));
    }
}
