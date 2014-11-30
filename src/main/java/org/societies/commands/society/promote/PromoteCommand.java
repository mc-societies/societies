package org.societies.commands.society.promote;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.promote", async = true)
@Permission("societies.promote")
@Meta(@Entry(key = RuleStep.RULE, value = "promote"))
@Sender(Member.class)
public class PromoteCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    private final Rank superDefaultRank;

    @Inject
    public PromoteCommand(@Named("super-default-rank") Rank superDefaultRank) {
        this.superDefaultRank = superDefaultRank;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        if (!group.equals(target.getGroup())) {
            sender.send("target-member.not-same-group", target.getName());
            return;
        }

        if (target.hasRank(superDefaultRank)) {
            sender.send("target-member.already-promoted", target.getName());
            return;
        }

        target.addRank(superDefaultRank);

        if (!target.equals(sender)) {
            sender.send("target-member.promoted", target.getName());
        }

        target.send("you.promoted-by", sender.getName());
    }
}
