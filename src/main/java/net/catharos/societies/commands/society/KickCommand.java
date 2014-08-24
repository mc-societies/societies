package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.kick")
public class KickCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.member.target")
    Member target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
        Group group = target.getGroup();

        if (group == null || !target.getGroup().equals(sender.getGroup())) {
            sender.send("not.same.group", target.getName());
            return;
        }

        sender.send("you.kicked.member", target.getName());
        target.send("kicked", group.getName());
        group.removeMember(target);
    }
}
