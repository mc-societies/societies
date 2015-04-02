package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.shank.config.ConfigSetting;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;

import javax.inject.Provider;
import java.util.Set;

/**
 * Represents a SocietiesListCommand
 */
@Command(identifier = "command.list", async = true)
@Permission("societies.list")
public class ListCommand implements Executor<Sender> {

    private final boolean showUnverified;
    private final GroupProvider groupProvider;
    private final Provider<Table> tableProvider;

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
        Set<Group> groups = groupProvider.getGroups();

        if (groups.isEmpty()) {
            sender.send("societies.not-found");
            return;
        }

        sender.send("Total societies: {0}", groups.size());

        Table table = tableProvider.get();

        table.addRow("Society", "Tag", "Members");

        for (Group group : groups) {
            Society society = group.get(Society.class);
            if ((!verified && showUnverified) || society.isVerified()) {
                table.addRow(group.getName(), group.getTag(), Integer.toString(group.getMembers().size()));
            }
        }

        sender.send(table.render(ctx.getName(), page));
    }
}
