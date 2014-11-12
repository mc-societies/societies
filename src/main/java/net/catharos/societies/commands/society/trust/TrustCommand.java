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
@Command(identifier = "command.trust")
@Permission("societies.trust")
@Meta(@Entry(key = RuleStep.RULE, value = "trust"))
@Sender(Member.class)
public class TrustCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    private final Rank normalDefaultRank;

    @Inject
    public TrustCommand(@Named("normal-default-rank") Rank normalDefaultRank) {
        this.normalDefaultRank = normalDefaultRank;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        if (target.hasRank(normalDefaultRank)) {
            sender.send("target-member.already-trusted", target.getName());
            return;
        }

        target.addRank(normalDefaultRank);
        sender.send("target-member.trusted", target.getName());
    }
}
