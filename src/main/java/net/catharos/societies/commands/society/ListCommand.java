package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Set;

/**
* Represents a SocietiesListCommand
*/
@Command(identifier = "list", description = "A default description!")
public class ListCommand implements Executor<SocietyMember> {

    private final GroupProvider groupProvider;
    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    @Inject
    public ListCommand(GroupProvider groupProvider, Provider<Table> tableProvider, RowFactory rowFactory) {
        this.groupProvider = groupProvider;
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Override
    public void execute(CommandContext<SocietyMember> ctx, final SocietyMember sender) {
        ListenableFuture<Set<Group>> future = groupProvider.getGroups();

        Futures.addCallback(future, new FutureCallback<Set<Group>>() {
            @Override
            public void onSuccess(@Nullable Set<Group> result) {
                if (result == null) {
                    return;
                }

                Table table = tableProvider.get();

                for (Group group : result) {
                    table.addRow(rowFactory.create(group));
                }

                sender.send(table.render());
            }

            @Override
            public void onFailure(@NotNull Throwable t) {

            }
        });
    }
}
