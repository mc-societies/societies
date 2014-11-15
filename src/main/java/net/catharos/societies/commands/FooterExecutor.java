package net.catharos.societies.commands;

import com.google.common.util.concurrent.ListenableFuture;
import net.catharos.bridge.ChatColor;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static com.google.common.util.concurrent.MoreExecutors.newDirectExecutorService;


/**
 * Represents a PreCommandStep
 */
class FooterExecutor implements Executor<Sender> {

    @Override
    public void execute(CommandContext<Sender> ctx, final Sender sender) throws ExecuteException {
        final Integer length = ctx.get("pre-length");

        if (length == null) {
            return;
        }

        ListenableFuture future = ctx.get("future");

        if (future != null) {
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    send(sender, length);
                }
            }, listeningDecorator(newDirectExecutorService()));
        } else {
            send(sender, length);
        }
    }


    private void send(Sender sender, int length) {
        sender.send(ChatColor.DARK_GRAY + StringUtils.repeat('-', length));
    }
}
