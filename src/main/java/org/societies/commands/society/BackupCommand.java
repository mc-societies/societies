package org.societies.commands.society;

import com.google.inject.Inject;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.Argument;
import order.reflect.Command;
import order.reflect.Permission;
import order.sender.Sender;
import org.societies.api.Saveguard;

import java.io.File;
import java.io.IOException;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.backup", async = true)
@Permission("societies.backup")
public class BackupCommand implements Executor<Sender> {

    @Argument(name = "argument.target.file")
    File output;

    private final Saveguard saveguard;

    @Inject
    public BackupCommand(Saveguard saveguard) {
        this.saveguard = saveguard;
    }


    @Override
    public void execute(CommandContext<Sender> ctx, final Sender sender) throws ExecuteException {
        try {
            saveguard.backup(output);
        } catch (IOException e) {
            sender.send(e.getMessage());
            return;
        }

        sender.send("backup.groups-finished");
        sender.send("backup.members-finished");
    }
}
