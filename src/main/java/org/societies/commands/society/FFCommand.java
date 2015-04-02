package org.societies.commands.society;

import order.CommandContext;
import order.Executor;
import order.reflect.Command;
import order.reflect.Permission;
import order.reflect.Sender;
import org.societies.api.member.SocietyMember;
import org.societies.groups.member.Member;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.ff")
@Permission("societies.ff")
@Sender(Member.class)
public class FFCommand implements Executor<Member> {

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {

        SocietyMember member = sender.get(SocietyMember.class);
        boolean ff = member.isFriendlyFire();
        member.setFirendlyFire(!ff);
        sender.send("personal-ff.toggled", !ff ? ":ff.allow" : ":ff.auto");
    }
}
