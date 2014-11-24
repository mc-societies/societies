package net.catharos.societies.commands.society.trust;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.trust", async = true)
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
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        if (!group.equals(target.getGroup())) {
            sender.send("target-member.not-same-group", target.getName());
            return;
        }

        if (target.hasRank(normalDefaultRank)) {
            sender.send("target-member.already-trusted", target.getName());
            return;
        }

        target.addRank(normalDefaultRank);
        sender.send("target-member.trusted", target.getName());
        target.send("you.trusted-by", sender.getName());
    }
}
