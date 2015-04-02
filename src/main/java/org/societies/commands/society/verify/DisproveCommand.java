package org.societies.commands.society.verify;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.disprove", async = true)
@Permission("societies.disprove")
public class DisproveCommand implements Executor<Sender> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        Society society = target.get(Society.class);
        society.setVerified(false);
        sender.send("target-society.disproved", target.getTag());
    }
}
