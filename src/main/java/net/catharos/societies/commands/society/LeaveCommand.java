package net.catharos.societies.commands.society;

import com.google.common.base.Function;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.GroupDropPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.collections.IterableUtils;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.societies.api.member.SocietyMember;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.leave")
@Permission("societies.leave")
@Sender(Member.class)
public class LeaveCommand implements Executor<SocietyMember> {

    private final GroupDropPublisher dropGroup;

    @Inject
    public LeaveCommand(GroupDropPublisher dropGroup) {this.dropGroup = dropGroup;}

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group != null) {
            Set<Member> leaders = group.getMembers("leader");

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


            group.removeMember(sender);

            if (group.size() == 0) {
                dropGroup.drop(group);
            }

            sender.send("you.society-left", group.getName());
            return;
        }

        sender.send("society.not.found!");
    }
}
