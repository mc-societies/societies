package net.catharos.societies.commands;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupPublisher;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Represents a ClanCommand
 */
@Command(identifier = "society", description = "A default description!")
@Children(children = {SocietyCommand.CreateCommand.class, SocietyCommand.SocietiesListCommand.class})
public class SocietyCommand {


    @Command(identifier = "create", description = "A default description!")
    public static class CreateCommand implements Executor<SocietyMember> {


        @Argument(name = "name", description = "The name of the new society")
        protected String name;

        private final GroupFactory groupFactory;
        private final GroupPublisher publisher;

        @Inject
        public CreateCommand(GroupFactory groupFactory, GroupPublisher publisher) {
            this.groupFactory = groupFactory;
            this.publisher = publisher;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            Group group = groupFactory.create(name);
            ListenableFuture<Group> future = publisher.publish(group);

            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            sender.send("%s created!", name);
        }
    }

    @Command(identifier = "list", description = "A default description!")
    public static class SocietiesListCommand implements Executor<SocietyMember> {

        private final GroupProvider groupProvider;
        private final Provider<Table> tableProvider;
        private final RowFactory rowFactory;

        @Inject
        public SocietiesListCommand(GroupProvider groupProvider, Provider<Table> tableProvider, RowFactory rowFactory) {
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
}
