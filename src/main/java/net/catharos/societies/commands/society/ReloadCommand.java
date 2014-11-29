package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.bridge.ReloadAction;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.reload")
@Permission("societies.reload")
public class ReloadCommand implements Executor<Sender> {

    private final ReloadAction reloadAction;

    @Inject
    public ReloadCommand(ReloadAction reloadAction) {this.reloadAction = reloadAction;}

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        sender.send("reload.started");
        reloadAction.reload();
        sender.send("reload.finished");
    }
}
