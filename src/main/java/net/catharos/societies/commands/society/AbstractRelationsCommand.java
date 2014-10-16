package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Represents a AbstractRelationsCommand
 */
public abstract class AbstractRelationsCommand implements Executor<Sender> {

    private final GroupProvider groupProvider;
    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    public AbstractRelationsCommand(GroupProvider groupProvider, Provider<Table> tableProvider, RowFactory rowFactory) {
        this.groupProvider = groupProvider;
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Option(name = "argument.page")
    int page;

    @Override
    public void execute(final CommandContext<Sender> ctx, final Sender sender) {
        ListenableFuture<Set<Group>> future = groupProvider.getGroups();

        Futures.addCallback(future, new FutureCallback<Set<Group>>() {
            @Override
            public void onSuccess(@Nullable Set<Group> result) {
                if (result == null) {
                    return;
                }

                if (result.isEmpty()) {
                    sender.send("societies.not-found");
                    return;
                }

                Table table = tableProvider.get();

                table.addForwardingRow(rowFactory.translated(true, "society", getType().getName()));

                for (Group group : result) {
                    Collection<Relation> relations = group.getRelations();

                    if (relations.isEmpty()) {
                        continue;
                    }

                    StringBuilder rivals = new StringBuilder();

                    for (Relation relation : relations) {
                        if (relation.getType() == getType()) {
                            Group relationGroup = null;

                            try {
                                relationGroup = groupProvider.getGroup(relation.getTarget()).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            if (relationGroup == null) {
                                continue;
                            }

                            rivals.append(relationGroup.getTag()).append(" + ");
                        }
                    }


                    table.addRow(group.getName(), rivals);
                }

                sender.send(table.render(ctx.getName(), page));
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                t.printStackTrace();
            }
        });

        ctx.put("future", future);
    }

    protected abstract Relation.Type getType();
}