package net.catharos.societies.commands.society.relation;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a RelationCreateCommand
 */
@Command(identifier = "command.relation.remove")
//todoCommands
public class RemoveCommand implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {

    }
}
