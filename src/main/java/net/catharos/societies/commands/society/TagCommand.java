package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.ChatColor;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.tag")
@Permission("societies.tag")
@Meta(@Entry(key = RuleStep.RULE, value = "tag"))
public class TagCommand implements Executor<Sender> {

    @Argument(name = "argument.society.tag-new")
    String newTag;

    @Option(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if ((sender instanceof SocietyMember)) {
            if (target == null) {
                target = ((SocietyMember) sender).getGroup();
            } else {
                sender.send("society.not-found");
            }
        }

        if (target == null) {
            sender.send("target-society.not-specified");
            return;
        }

        String sourceTag = ChatColor.stripColor(target.getTag());
        String targetTag = ChatColor.stripColor(newTag);


        if (!sourceTag.equals(targetTag)) {
            sender.send("target-society.tag-only-colors");
            return;
        }

        target.setTag(newTag);
        sender.send("target-society.tag-modified");
    }
}
