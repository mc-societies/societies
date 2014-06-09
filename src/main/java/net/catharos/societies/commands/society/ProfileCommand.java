package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "profile", description = "A default description!")
public class ProfileCommand implements Executor<SocietyMember> {

    @Option(name = "target")
    Group target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        if (target == null) {
            target = sender.getGroup();
        }

        if (target == null) {
            sender.send("Society not found!");
            return;
        }

        sender.send("Profile: " + target.getName());
    }
}
