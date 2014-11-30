package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.GroupDropPublisher;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.leave")
@Permission("societies.leave")
@Sender(Member.class)
public class LeaveCommand implements Executor<Member> {

    private final GroupDropPublisher dropGroup;

    @Inject
    public LeaveCommand(GroupDropPublisher dropGroup) {this.dropGroup = dropGroup;}

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group != null) {

            if (KickCommand.isCritical(sender, sender, group)) {
                return;
            }

            group.removeMember(sender);

            if (group.size() == 0) {
                dropGroup.drop(group);
            }

            sender.send("you.society-left", group.getName());
            return;
        }

        sender.send("society.not.found");
    }
}
