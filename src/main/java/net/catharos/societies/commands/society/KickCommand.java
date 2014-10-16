package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.kick")
@Permission("societies.kick")
@Meta(@Entry(key = RuleStep.RULE, value = "kick"))
@Sender(Member.class)
public class KickCommand implements Executor<Member> {

    @Argument(name = "argument.target.member")
    Member target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = target.getGroup();

        if (group == null || !target.getGroup().equals(sender.getGroup())) {
            sender.send("member.not-same-group", target.getName());
            return;
        }

        sender.send("you.kicked-member", target.getName());
        target.send("member.kicked", group.getName());
        group.removeMember(target);
    }
}
