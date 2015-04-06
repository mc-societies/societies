package org.societies.commands.society;

import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.*;
import org.bukkit.ChatColor;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.util.ChatUtil;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.tag")
@Permission("societies.tag")
@Meta(@Entry(key = RuleStep.RULE, value = "tag"))
@Sender(Member.class)
public class TagCommand implements Executor<Member> {

    @Argument(name = "argument.society.tag-new")
    String newTag;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        String sourceTag = ChatColor.stripColor(group.getTag());
        String targetTag = ChatUtil.stripUserColor(newTag);


        if (!sourceTag.equals(targetTag)) {
            sender.send("target-society.tag-only-colors");
            return;
        }


        group.setTag(ChatColor.translateAlternateColorCodes('&', newTag));
        sender.send("target-society.tag-modified");
    }
}
