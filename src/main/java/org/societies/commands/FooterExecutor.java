package org.societies.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.ChatColor;


/**
 * Represents a PreCommandStep
 */
class FooterExecutor implements Executor<Sender> {


    private final Formatter formatter;
    private final WidthProvider widthProvider;

    @Inject
    FooterExecutor(Formatter formatter, WidthProvider widthProvider) {
        this.formatter = formatter;
        this.widthProvider = widthProvider;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, final Sender sender) throws ExecuteException {
        StringBuilder builder = new StringBuilder();
        formatter.fill(builder, widthProvider.widthOf(builder), '-');
        sender.send(ChatColor.DARK_GRAY + builder.toString());
    }
}
