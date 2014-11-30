package org.societies.commands.society;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.Player;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.rename", async = true)
@Permission("societies.rename")
public class RenameCommand implements Executor<Sender> {

    @Argument(name = "argument.society.name-new")
    String newName;

    @Option(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if ((sender instanceof Player)) {
            if (target == null) {
                target = ((Member) sender).getGroup();
            } else {
                sender.send("society.not-found");
            }
        }

        if (target == null) {
            sender.send("target-society.not-specified");
            return;
        }

        target.setName(newName);
    }
}
