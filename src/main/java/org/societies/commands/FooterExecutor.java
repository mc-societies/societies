package org.societies.commands;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.Formatter;
import net.catharos.lib.core.command.format.WidthProvider;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.ChatColor;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;


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
        ListenableFuture future = ctx.get("future");

        if (future != null) {
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    send(sender);
                }
            }, listeningDecorator(newDirectExecutorService()));
        } else {
            send(sender);
        }
    }


    private void send(Sender sender) {
        StringBuilder builder = new StringBuilder();
        formatter.fill(builder, widthProvider.widthOf(builder), '-');
        sender.send(ChatColor.DARK_GRAY + builder.toString());
    }
}
