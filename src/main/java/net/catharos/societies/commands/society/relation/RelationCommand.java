package net.catharos.societies.commands.society.relation;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Relation;
import net.catharos.groups.RelationFactory;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.member.SocietyMember;

import javax.inject.Provider;
import java.util.Collection;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.relation.relation")
@Children({
        RelationCommand.CreateCommand.class,
        RelationCommand.RemoveCommand.class,
        RelationCommand.ListCommand.class
})
public class RelationCommand {


    @Command(identifier = "command.relation.remove")
    public static class RemoveCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.target.society")
        Group target;

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            group.removeRelation(target);

            sender.send("relation.removed", target.getName());
        }
    }


    @Command(identifier = "command.relation.list")
    public static class ListCommand implements Executor<SocietyMember> {

        private final Provider<Table> tableProvider;

        @Option(name = "argument.page")
        int page;

        @Inject
        public ListCommand(Provider<Table> tableProvider) {
            this.tableProvider = tableProvider;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Collection<Relation> relations = group.getRelations();

            if (relations.isEmpty()) {
                sender.send("relations.not-found");
                return;
            }

            Table table = tableProvider.get();

            for (Relation relation : relations) {
                table.addForwardingRow(relation);
            }

            sender.send(table.render(ctx.getName(), page));
        }
    }


    @Command(identifier = "command.relation.create", async = true)
    public static class CreateCommand implements Executor<SocietyMember> {

        @Argument(name = "argument.target.society")
        Group target;

        private final RelationFactory factory;

        @Inject
        public CreateCommand(RelationFactory factory) {this.factory = factory;}

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Relation relation = factory.create(group, target);

            group.setRelation(target, relation);

            sender.send("relation.created", target.getName());
        }
    }

}
