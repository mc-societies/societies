package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "profile", description = "A default description!")
public class ProfileCommand implements Executor<Sender> {

    @Option(name = "target")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender).getGroup();
        }

        if (target == null) {
            sender.send("Society not found!");
            return;
        }

        sender.send(target.getName());

        sender.send("Members: ");

        for (Member member : target.getMembers()) {
            sender.send(member.getUUID().toString());
        }

    }
}
