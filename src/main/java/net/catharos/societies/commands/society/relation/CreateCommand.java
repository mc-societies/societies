package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.RelationFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a RelationCreateCommand
 */
@Command(identifier = "command.relation.create", async = true)
public class CreateCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.society.target")
    Group target;

    private final RelationFactory factory;

    public CreateCommand(RelationFactory factory) {this.factory = factory;}

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        factory.create(sender.getGroup(), target);
    }
}
