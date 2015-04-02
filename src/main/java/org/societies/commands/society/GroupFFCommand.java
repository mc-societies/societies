package org.societies.commands.society;

import order.CommandContext;
import order.Executor;
import order.reflect.*;
import org.societies.api.group.Society;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.group-ff")
@Permission("societies.group-ff")
@Meta(@Entry(key = RuleStep.RULE, value = "ff"))
@Sender(Member.class)
public class GroupFFCommand implements Executor<Member> {

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Society society = group.get(Society.class);
        boolean ff = society.isFriendlyFire();
        society.setFriendlyFire(!ff);

        sender.send("group-ff.toggled", !ff ? ":ff.allow" : ":ff.deny");
    }
}
