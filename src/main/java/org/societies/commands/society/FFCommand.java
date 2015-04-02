package org.societies.commands.society;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.groups.member.Member;
import org.societies.api.member.SocietyMember;

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
