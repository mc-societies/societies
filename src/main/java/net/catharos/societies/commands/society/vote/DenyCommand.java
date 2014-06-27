package net.catharos.societies.commands.society.vote;

import net.catharos.groups.request.Request;
import net.catharos.groups.request.SimpleRequest;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "deny", description = "A default description!")
public class DenyCommand implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Request activeRequest = sender.getActiveRequest();

        if (activeRequest == null) {
            return;
        }

        activeRequest.vote(sender, SimpleRequest.Choices.DENY);
    }
}
