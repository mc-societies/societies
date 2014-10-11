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
import net.catharos.societies.bukkit.PlayerState;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.vitals")
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
            Player player = ((SocietyMember) member).toPlayer();
            if (player == null) {
                continue;
            }

            PlayerState state = new PlayerState(player);

            table.addRow(member.getName(), state.getHealth(), state.getArmor("H", "C", "L", "B"), state
                    .getWeapons("S", "B", "A"), state.getHunger());
        }

        sender.send(table.render(ctx.getName(), page));

    }
}
