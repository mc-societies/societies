package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Relation;
import net.catharos.groups.RelationFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.member.SocietyMember;

import java.util.Collection;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.relation")
@Children(children = {
        RelationCommand.CreateCommand.class,
        RelationCommand.RemoveCommand.class,
        RelationCommand.ListCommand.class
})
public class RelationCommand {


    @Command(identifier = "command.relation.remove")
    public static class RemoveCommand implements Executor<SocietyMember> {

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


    @Command(identifier = "command.relation.list")
    public static class ListCommand implements Executor<SocietyMember> {

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


    @Command(identifier = "command.relation.create", async = true)
    public static class CreateCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.society.target")
        Group target;

        private final RelationFactory factory;

        public CreateCommand(RelationFactory factory) {this.factory = factory;}

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not.found");
                return;
            }

            Relation relation = factory.create(group, target);

            group.setRelation(relation);

            sender.send("relation.created", target.getName());
        }
    }

}
