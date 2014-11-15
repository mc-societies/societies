package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.societies.api.member.SocietyMember;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.leave")
@Permission("societies.leave")
public class LeaveCommand implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group != null) {
            group.removeMember(sender);

            if (group.size() == 1) {
                //fixme drop group
                //fixme if last leader leaves choose another one
                return;
            }

            sender.send("you.society-left",  group.getName());
            return;
        }

        sender.send("society.not.found!");
    }
}
