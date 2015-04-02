package org.societies.commands.society.trust;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import order.CommandContext;
import order.Executor;
import order.reflect.*;
import org.societies.IterableUtils;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

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
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        if (!group.equals(target.getGroup())) {
            sender.send("target-member.not-same-group", target.getName());
            return;
        }

        if (!target.hasRank(normalDefaultRank)) {
            sender.send("target-member.not-trusted", target.getName());
            return;
        }

        Set<Member> cantrust = group.getMembers("trust");

        //beautify
        if (cantrust.size() <= 1) {
            Collection<Rank> trustRanks = group.getRanks("trust");
            String leaderRanksString = IterableUtils.toString(trustRanks, new Function<Rank, String>() {
                @Nullable
                @Override
                public String apply(Rank input) {
                    return input.getName();
                }
            });
            sender.send("you.assign-first", leaderRanksString);
            return;
        }

        target.removeRank(normalDefaultRank);
        if (!target.equals(sender)) {
            sender.send("target-member.distrusted", target.getName());
        }
        target.send("you.distrusted-by", sender.getName());
    }
}
