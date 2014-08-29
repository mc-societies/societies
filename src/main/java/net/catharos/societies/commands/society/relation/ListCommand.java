package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;

import java.util.Collection;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.relation.list")
public class ListCommand  implements Executor<SocietyMember> {

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not.found");
            return;
        }

        Collection<Relation> relations = group.getRelations();

        for (Relation relation : relations) {
            sender.send(relation.getSource().getName() + " - " + relation.getTarget().getName());
        }
    }
}
