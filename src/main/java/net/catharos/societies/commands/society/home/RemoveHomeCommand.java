package net.catharos.societies.commands.society.home;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.rename", async = true)
//todoCommands
public class RemoveHomeCommand implements Executor<Sender> {

    @Argument(name = "argument.society.name.new")
    String newName;

    @Option(name = "argument.society.target")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if ((sender instanceof SocietyMember)) {
            if (target == null) {
                target = ((SocietyMember) sender).getGroup();
            } else {
                sender.send("society.not.found");
            }
        }

        if (target == null) {
            sender.send("target.society.not.specified");
            return;
        }

        target.setName(newName);
    }
}
