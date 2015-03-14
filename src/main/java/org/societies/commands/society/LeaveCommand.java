package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupPublisher;
import org.societies.groups.member.Member;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.leave")
@Permission("societies.leave")
@Sender(Member.class)
public class LeaveCommand implements Executor<Member> {

    private final GroupPublisher dropGroup;

    @Inject
    public LeaveCommand(GroupPublisher dropGroup) {
        this.dropGroup = dropGroup;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group != null) {

            if (group.size() == 1) {
                group.removeMember(sender);
                dropGroup.destruct(group);
            } else if (KickCommand.isCritical(sender, sender, group)) {
                return;
            } else {
                group.removeMember(sender);
            }

            String name = group.getName();

            sender.send("you.society-left", name);
            return;
        }

        sender.send("society.not-found");
    }
}
