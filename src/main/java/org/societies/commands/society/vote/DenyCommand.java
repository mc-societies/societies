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
@Command(identifier = "command.vote.deny")
@Permission("societies.vote.deny")
@Sender(Member.class)
public class DenyCommand implements Executor<Member> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Request activeRequest = sender.getReceivedRequest();

        if (activeRequest == null) {
            sender.send("request.none-received=");
            return;
        }

        try {
            activeRequest.vote(sender, Choices.DENY);
            sender.send("request.voted.deny");
        } catch (ClassCastException ignored) {
        }
    }
}
