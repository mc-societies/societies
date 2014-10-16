package net.catharos.societies.commands.society.relation;

import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;

import javax.inject.Provider;
import java.util.Collection;

/**
 * Represents a ListCommand
 */
abstract class ListCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;

    @Option(name = "argument.page")
    int page;

    public ListCommand(Provider<Table> tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
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

    protected abstract Relation.Type getType();
}
