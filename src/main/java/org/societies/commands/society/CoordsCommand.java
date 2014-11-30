package org.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import org.societies.bridge.Location;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.commands.VerifyStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import javax.inject.Provider;
import java.text.DecimalFormat;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.coords")
@Permission("societies.coords")
@Meta({@Entry(key = RuleStep.RULE, value = "coords"), @Entry(key = VerifyStep.VERIFY)})
@Sender(Member.class)
public class CoordsCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    private static final DecimalFormat numberFormat = new DecimalFormat("#.#");

    @Option(name = "argument.page")
    int page;

    @Inject
    public CoordsCommand(Provider<Table> tableProvider, RowFactory rowFactory) {
        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Location location = sender.get(Player.class).getLocation();

        assert location != null;

        Table table = tableProvider.get();

        table.addForwardingRow(rowFactory.translated(true, "name", "distance", "coordinates", "world"));

        for (Member member : group.getMembers()) {
            Location memberLocation = member.get(Player.class).getLocation();

            if (memberLocation == null) {
                continue;
            }

            table.addRow(
                    member.getName(),
                    numberFormat.format(location.distance(memberLocation)),
                    "X: " + memberLocation.getRoundedX()
                            + " Y: " + memberLocation.getRoundedY()
                            + " Z: " + memberLocation.getRoundedZ(),
                    memberLocation.getWorld()
            );
        }

        sender.send(table.render(ctx.getName(), page));

    }
}
