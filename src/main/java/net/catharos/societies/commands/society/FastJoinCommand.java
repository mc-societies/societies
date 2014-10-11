package net.catharos.societies.commands.society;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.fastjoin", async = true)
@Sender(value = Member.class)
public class FastJoinCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        sender.setGroup(target);
    }
}
