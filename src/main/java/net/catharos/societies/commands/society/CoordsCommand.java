package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.coords")
@Sender(SocietyMember.class)
public class CoordsCommand implements Executor<SocietyMember> {

    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    @Option(name = "argument.page")
    int page;

    @Inject
    public CoordsCommand(Provider<Table> tableProvider, RowFactory rowFactory) {
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Player senderPlayer = sender.toPlayer();

        assert senderPlayer != null;

        Location location = senderPlayer.getLocation();

        Table table = tableProvider.get();

        table.addForwardingRow(rowFactory.translated(true, "name", "distance", "coordinates", "world"));

        for (Member member : group.getMembers()) {
            Player player = ((SocietyMember) member).toPlayer();
            if (player == null) {
                continue;
            }

            Location memberLocation = player.getLocation();
            table.addRow(member.getName(), location.distance(memberLocation), memberLocation, memberLocation.getWorld().getName());
        }

        sender.send(table.render(ctx.getName(), page));

    }
}
