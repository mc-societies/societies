package net.catharos.societies.commands.society.rank;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a RemoveCommand
 */
@Command(identifier = "remove", description = "A default description!")
public class RemoveCommand  implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {

    }
}
