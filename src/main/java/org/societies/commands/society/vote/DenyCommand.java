package org.societies.commands.society.vote;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.groups.member.Member;
import org.societies.groups.request.Request;
import org.societies.groups.request.simple.Choices;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vote.deny")
@Permission("societies.vote.deny")
@Sender(Member.class)
public class DenyCommand implements Executor<Member> {

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Request activeRequest = sender.getReceivedRequest();

        if (activeRequest == null) {
            sender.send("request.none-received=");
            return;
        }

        sender.send("request.voted.deny");
        activeRequest.vote(sender, Choices.DENY);
    }
}
