package org.societies.commands;

import com.google.inject.Inject;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.format.Formatter;
import order.format.WidthProvider;
import order.sender.Sender;
import org.bukkit.ChatColor;


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
