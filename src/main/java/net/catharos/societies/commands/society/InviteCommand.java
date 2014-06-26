package net.catharos.societies.commands.society;

import net.catharos.groups.request.SimpleRequest;
import net.catharos.groups.request.SingularInvolved;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a InviteCommand
 */
public class InviteCommand implements Executor<SocietyMember> {

    @Argument(name = "target", description = "The target member to invite")
    SocietyMember target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        SimpleRequest request = new SimpleRequest(new SingularInvolved(target));
        target.setActiveRequest(request);
    }
}
