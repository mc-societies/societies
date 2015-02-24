package org.societies.commands.society;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.setting.Setting;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.group-ff")
@Permission("societies.group-ff")
@Meta(@Entry(key = RuleStep.RULE, value = "ff"))
@Sender(Member.class)
public class GroupFFCommand implements Executor<Member> {

    private final Setting<Boolean> groupFF;

    @Inject
    public GroupFFCommand(@Named("group-ff") Setting<Boolean> groupFF) {
        this.groupFF = groupFF;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        boolean ff = group.getBoolean(groupFF);
        group.set(groupFF, !ff);
        sender.send("group-ff.toggled", !ff ? ":ff.allow" : ":ff.deny");
    }
}
