package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.profile")
public class ProfileCommand implements Executor<Sender> {

    @Option(name = "argument.target.society")
    Group target;

    @Option(name = "v")
    boolean v;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender).getGroup();
        }

        if (target == null) {
            sender.send("target-society.not-specified");
            return;
        }

        sender.send("Name: " + target.getName());
        if (v) {
            sender.send("UUID: " + target.getUUID());
        }
        sender.send("Last active: " + target.getLastActive());
        sender.send("Inactive: " + new Interval(target.getLastActive(), DateTime.now()));
        //todo
        sender.send("Founded");
        sender.send("Members Online: ");
        sender.send("Allies");
        sender.send("Rivals");
    }
}
