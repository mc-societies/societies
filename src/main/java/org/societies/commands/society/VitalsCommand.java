package org.societies.commands.society;

import com.google.inject.Inject;
import order.CommandContext;
import order.Executor;
import order.format.table.RowFactory;
import order.format.table.Table;
import order.reflect.*;
import org.societies.PlayerState;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.commands.VerifyStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vitals")
@Permission("societies.vitals")
@Meta({@Entry(key = RuleStep.RULE, value = "vitals"), @Entry(key = VerifyStep.VERIFY)})
@Sender(Member.class)
public class VitalsCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    @Option(name = "argument.page")
    int page;

    @Inject
    public VitalsCommand(Provider<Table> tableProvider, RowFactory rowFactory) {

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

        Table table = tableProvider.get();

        table.addForwardingRow(rowFactory.translated(true, "name", "health", "armor", "weapons", "food"));

        for (Member member : group.getMembers()) {
            Player player = member.get(Player.class);
            if (player == null) {
                continue;
            }

            PlayerState state = new PlayerState(player);

            table.addRow(
                    member.getName(),
                    state.getHealth(),
                    state.getArmor("H", "C", "L", "B"),
                    state.getWeapons("S", "B", "A"),
                    state.getHunger()
            );
        }

        sender.send(table.render(ctx.getName(), page));
    }
}
