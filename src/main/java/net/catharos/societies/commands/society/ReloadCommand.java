package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.launcher.ReloadAction;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.reload")
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
