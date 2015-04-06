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
@Command(identifier = "command.vote.abstain")
@Permission("societies.vote.abstain")
@Sender(Member.class)
public class AbstainCommand implements Executor<Member> {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Request activeRequest = sender.getReceivedRequest();

        if (activeRequest == null) {
            sender.send("request.none-received");
            return;
        }

        try {
            sender.send("request.voted.abstain");
            activeRequest.vote(sender, Choices.ABSTAIN);
        } catch (ClassCastException ignored) {
        }
    }
}
