package net.catharos.societies.commands.society;

import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.group-ff")
@Permission("societies.group-ff")
@Meta(@Entry(key = RuleStep.RULE, value = "ff"))
@Sender(Member.class)
public class GroupFFCommand implements Executor<Member> {

    private final Setting<Boolean> groupFF;

    public GroupFFCommand(@Named("group-friendly-fire") Setting<Boolean> groupFF) {
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
        sender.set(groupFF, !ff);
        sender.send("group-ff.toggled", ff ? "ff.allow" : "ff.deny");
    }
}
