package org.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.shank.config.ConfigSetting;
import org.shank.logging.InjectLogger;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Set;

/**
 * Represents a SocietiesListCommand
 */
@Command(identifier = "command.list")
@Permission("societies.list")
public class ListCommand implements Executor<Sender> {

    private final boolean showUnverified;
    private final GroupProvider groupProvider;
    private final Provider<Table> tableProvider;

    @InjectLogger
    private Logger logger;

    @Inject
    public ListCommand(@ConfigSetting("verification.show-unverified") boolean showUnverified, GroupProvider groupProvider, Provider<Table> tableProvider) {
        this.showUnverified = showUnverified;
        this.groupProvider = groupProvider;
        this.tableProvider = tableProvider;
    }

    @Option(name = "argument.page")
    int page;

    @Option(name = "argument.verified")
    boolean verified;

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

                sender.send("Total societies: {0}", result.size());

                Table table = tableProvider.get();

                table.addRow("Society", "Tag", "Members");

                for (Group group : result) {
                    if ((!verified && showUnverified) || group.isVerified()) {
                        table.addRow(group.getName(), group.getTag(), Integer.toString(group.getMembers().size()));
                    }
                }

                sender.send(table.render(ctx.getName(), page));
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                logger.catching(t);
            }
        });

        ctx.put("future", future);
    }
}
