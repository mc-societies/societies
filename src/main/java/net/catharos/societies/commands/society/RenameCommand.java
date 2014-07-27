package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "rename", description = "A default description!", async = true)
public class RenameCommand implements Executor<Sender> {

    @Argument(name = "newName")
    String newName;

    @Option(name = "target", description = "")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if ((sender instanceof SocietyMember) && target == null) {
            target = ((SocietyMember) sender).getGroup();
        }

        target.setName(newName);
    }
}
