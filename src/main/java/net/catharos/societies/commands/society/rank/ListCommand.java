package net.catharos.societies.commands.society.rank;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a ListCommand
 */
@Command(identifier = "list", description = "A default description!")
//todoCommands
public class ListCommand  implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {

    }
}
