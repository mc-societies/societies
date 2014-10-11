package net.catharos.societies.commands.society.vote;

import net.catharos.groups.request.simple.Choices;
import net.catharos.groups.request.Request;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vote.deny")
public class DenyCommand implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Request activeRequest = sender.getReceivedRequest();

        if (activeRequest == null) {
            sender.send("request.none-received=");
            return;
        }

        sender.send("request.voted.deny");
        activeRequest.vote(sender, Choices.DENY);
    }
}
