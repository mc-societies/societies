package org.societies.commands.society;

import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.Argument;
import order.reflect.Command;
import order.reflect.Option;
import order.reflect.Permission;
import order.sender.Sender;
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
        if ((sender instanceof Member)) {
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
