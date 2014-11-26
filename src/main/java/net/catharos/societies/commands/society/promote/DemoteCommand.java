package net.catharos.societies.commands.society.promote;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.collections.IterableUtils;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.demote", async = true)
@Permission("societies.demote")
@Meta(@Entry(key = RuleStep.RULE, value = "demote"))
@Sender(Member.class)
public class DemoteCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    private final Rank normalDefaultRank;

    @Inject
    public DemoteCommand(@Named("super-default-rank") Rank normalDefaultRank) {
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
            sender.send("target-member.not-promoted", target.getName());
            return;
        }

        Set<Member> leaders = group.getMembers("leader");

        //beautify
        if (leaders.contains(target)) {
            if (leaders.size() == 1) {
                Collection<Rank> leaderRanks = group.getRanks("leader");
                String leaderRanksString = IterableUtils.toString(leaderRanks, new Function<Rank, String>() {
                    @Nullable
                    @Override
                    public String apply(Rank input) {
                        return input.getName();
                    }
                });
                sender.send("you.assign-leader-first", leaderRanksString);
                return;
            }
        }

        target.removeRank(normalDefaultRank);

        if (!target.equals(sender)) {
            sender.send("target-member.demoted", target.getName());
        }

        target.send("you.demoted-by", sender.getName());
    }
}
