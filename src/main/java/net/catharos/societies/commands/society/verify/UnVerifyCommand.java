package net.catharos.societies.commands.society.verify;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.NameProvider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.unverify")
//todoCommands
public class UnVerifyCommand implements Executor<Sender> {

    @Option(name = "argument.society.target")
    Group target;

    private final NameProvider nameProvider;

    @Inject
    public UnVerifyCommand(NameProvider nameProvider) {this.nameProvider = nameProvider;}

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender).getGroup();
        }

        if (target == null) {
            sender.send("target.society.not.specified");
            return;
        }

        sender.send("Name: " + target.getName());
        sender.send("UUID: " + target.getUUID());
        sender.send("Last active: " + target.getLastActive());
        sender.send("Members:");
        for (Member member : target.getMembers()) {
            sender.send(" -" + nameProvider.getName(member.getUUID()));
        }

    }
}
