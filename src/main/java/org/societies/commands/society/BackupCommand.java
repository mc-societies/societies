package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
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
