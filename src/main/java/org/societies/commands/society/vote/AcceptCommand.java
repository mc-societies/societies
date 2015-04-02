package org.societies.commands.society.vote;

import order.CommandContext;
import order.Executor;
import order.reflect.Command;
import order.reflect.Permission;
import order.reflect.Sender;
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
