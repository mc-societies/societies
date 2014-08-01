package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.fastjoin", async = true)
public class FastJoinCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.society.target")
    Group target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
        sender.setGroup(target);
    }
}