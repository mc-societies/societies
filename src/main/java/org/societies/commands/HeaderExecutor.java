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
class HeaderExecutor implements Executor<Sender> {

    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();

    private final Formatter formatter;
    private final WidthProvider widthProvider;

    @Inject
    HeaderExecutor(Formatter formatter, WidthProvider widthProvider) {
        this.formatter = formatter;
        this.widthProvider = widthProvider;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        String name = GRAY + ctx.getCommand().getName() + DARK_GRAY + "   ";

        StringBuilder message = new StringBuilder(name);
        formatter.fill(message, widthProvider.widthOf(message), '-');
        sender.send(message);
    }
}
