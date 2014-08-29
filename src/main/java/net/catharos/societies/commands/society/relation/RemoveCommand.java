package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a RelationCreateCommand
 */
@Command(identifier = "command.relation.remove")
public class RemoveCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.society.target")
    Group target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not.found");
            return;
        }

        group.removeRelation(target);

        sender.send("relation.removed", target.getName());
    }
}
