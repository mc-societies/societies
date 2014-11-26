package net.catharos.societies.commands.society;

import com.google.common.base.Function;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.collections.IterableUtils;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.kick", async = true)
@Permission("societies.kick")
@Meta(@Entry(key = RuleStep.RULE, value = "kick"))
@Sender(Member.class)
public class KickCommand implements Executor<Member> {

    @Argument(name = "argument.target.member")
    Member target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = target.getGroup();

        if (group == null || !target.getGroup().equals(sender.getGroup())) {
            sender.send("target-member.not-same-group", target.getName());
            return;
        }

        Set<Member> leaders = group.getMembers("leader");

        if (leaders.contains(target)) {
            if (leaders.size() == 1 && group.size() > 1) {
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

        sender.send("you.kicked-member", target.getName(), group.getTag());
        target.send("member.kicked", group.getName());
        group.removeMember(target);
    }
}
