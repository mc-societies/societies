package org.societies.commands.society.relation;

import com.google.common.base.Optional;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;

import javax.inject.Provider;
import java.util.Collection;

/**
 * Represents a ListCommand
 */
abstract class ListCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final GroupProvider groupProvider;

    @Option(name = "argument.page")
    int page;

    public ListCommand(Provider<Table> tableProvider, GroupProvider groupProvider) {
        this.tableProvider = tableProvider;
        this.groupProvider = groupProvider;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Collection<Relation> relations = group.getRelations(getType());

        if (relations.isEmpty()) {
            sender.send("relations.not-found");
            return;
        }

        Table table = tableProvider.get();

        for (Relation relation : relations) {
            Optional<Group> target = groupProvider.getGroup(relation.getOpposite(group.getUUID()));

            if (!target.isPresent()) {
                continue;
            }

            table.addForwardingRow(target.get());
        }

        sender.send(table.render(ctx.getName(), page));
    }

    protected abstract Relation.Type getType();
}
