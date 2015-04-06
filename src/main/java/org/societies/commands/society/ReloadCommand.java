package org.societies.commands.society;

import com.google.inject.Inject;
import order.CommandContext;
import order.Executor;
import order.reflect.Command;
import order.reflect.Permission;
import order.sender.Sender;
import org.societies.api.ReloadAction;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.reload")
@Permission("societies.reload")
public class ReloadCommand implements Executor<Sender> {

    private final ReloadAction reloadAction;

    @Inject
    public ReloadCommand(ReloadAction reloadAction) {
        this.reloadAction = reloadAction;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        sender.send("reload.started");
        reloadAction.reload();
        sender.send("reload.finished");
    }
}
