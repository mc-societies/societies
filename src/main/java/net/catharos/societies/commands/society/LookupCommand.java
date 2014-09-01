package net.catharos.societies.commands.society;

import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.lookup")
public class LookupCommand implements Executor<Sender> {

    @Option(name = "argument.target.member")
    Member target;


    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender);
        }

        if (target == null) {
            sender.send("target.member.not.specified");
            return;
        }

        sender.send("Name: " + target.getName());
        sender.send("UUID: " + target.getUUID());
        sender.send("Group: " + target.getGroup());
        sender.send("Last Seen: ");
        sender.send("Inactive: ");
        sender.send("Join Date: ");
        sender.send("Ranks:");
        for (Rank rank : target.getRanks()) {
            sender.send(" -" + rank.getName());
        }

    }
}
