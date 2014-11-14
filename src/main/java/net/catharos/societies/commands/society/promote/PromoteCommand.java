package net.catharos.societies.commands.society.promote;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

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

    private final Rank normalDefaultRank;

    @Inject
    public PromoteCommand(@Named("super-default-rank") Rank normalDefaultRank) {
        this.normalDefaultRank = normalDefaultRank;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        if (target.hasRank(normalDefaultRank)) {
            sender.send("target-member.already-promoted", target.getName());
            return;
        }

        target.addRank(normalDefaultRank);
        sender.send("target-member.promoted", target.getName());
        target.send("you.promoted-by", sender.getName());
    }
}
