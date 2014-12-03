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
@Command(identifier = "command.vote.accept")
@Permission("societies.vote.accept")
@Sender(Member.class)
public class AcceptCommand implements Executor<Member> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Request activeRequest = sender.getReceivedRequest();

        if (activeRequest == null) {
            sender.send("request.none-received");
            return;
        }


        try {
            activeRequest.vote(sender, Choices.ACCEPT);
            sender.send("request.voted.accept");
        } catch (ClassCastException ignored) {
        }
    }
}
