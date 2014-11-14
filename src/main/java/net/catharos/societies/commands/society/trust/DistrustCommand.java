package net.catharos.societies.commands.society.trust;

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
@Command(identifier = "command.distrust", async = true)
@Permission("societies.distrust")
@Meta(@Entry(key = RuleStep.RULE, value = "distrust"))
@Sender(Member.class)
public class DistrustCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    private final Rank normalDefaultRank;

    @Inject
    public DistrustCommand(@Named("normal-default-rank") Rank normalDefaultRank) {
        this.normalDefaultRank = normalDefaultRank;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        if (!target.hasRank(normalDefaultRank)) {
            sender.send("target-member.not-trusted", target.getName());
            return;
        }

        target.removeRank(normalDefaultRank);
        sender.send("target-member.distrusted", target.getName());
        target.send("you.distrusted-by", sender.getName());
    }
}
