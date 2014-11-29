package net.catharos.societies.commands.society;

import net.catharos.bridge.ChatColor;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

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
        String targetTag = ChatColor.stripUserColor(newTag);


        if (!sourceTag.equals(targetTag)) {
            sender.send("target-society.tag-only-colors");
            return;
        }


        group.setTag(ChatColor.translateString(newTag));
        sender.send("target-society.tag-modified");
    }
}
