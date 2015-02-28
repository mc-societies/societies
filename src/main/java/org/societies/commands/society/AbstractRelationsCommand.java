package org.societies.commands.society;

import com.google.common.base.Optional;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import org.apache.logging.log4j.Logger;
import org.shank.logging.InjectLogger;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a AbstractRelationsCommand
 */
public abstract class AbstractRelationsCommand implements Executor<Sender> {

    private final GroupProvider groupProvider;
    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    @InjectLogger
    private Logger logger;

    public AbstractRelationsCommand(GroupProvider groupProvider, Provider<Table> tableProvider, RowFactory rowFactory) {
        this.groupProvider = groupProvider;
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Option(name = "argument.page")
    int page;

    @Override
    public void execute(final CommandContext<Sender> ctx, final Sender sender) {
        Set<Group> groups = groupProvider.getGroups();

        if (groups.isEmpty()) {
            sender.send("societies.not-found");
            return;
        }

        Table table = tableProvider.get();

        table.addForwardingRow(rowFactory.translated(true, "society", getType().getName()));

        for (Group group : groups) {
            Collection<Relation> relations = group.getRelations(getType());

            if (relations.isEmpty()) {
                continue;
            }

            StringBuilder relationsString = new StringBuilder();

            for (Relation relation : relations) {

                UUID target = relation.getOpposite(group.getUUID());

                Optional<Group> relationGroup = groupProvider.getGroup(target);

                if (!relationGroup.isPresent()) {
                    continue;
                }

                relationsString.append(relationGroup.get().getTag()).append(" + ");
            }

            int length = relationsString.length();

            if (length > 3) {
                relationsString.delete(length - 3, length);
            }

            table.addRow(group.getName(), relationsString);
        }

        sender.send(table.render(ctx.getName(), page));
    }

    protected abstract Relation.Type getType();
}
