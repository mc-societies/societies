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
import net.catharos.lib.core.command.sender.Sender;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.roster")
public class RosterCommand implements Executor<Sender> {

    @Option(name = "argument.society.target")
    Group target;

    private final Provider<Table> tableProvider;
    private final RowFactory rowFactory;

    @Option(name = "argument.page")
    int page;

    @Inject
    public RosterCommand(Provider<Table> tableProvider, RowFactory rowFactory) {

        this.tableProvider = tableProvider;
        this.rowFactory = rowFactory;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender).getGroup();
        }

        if (target == null) {
            sender.send("target.society.not.specified");
            return;
        }

        Table table = tableProvider.get();

        for (Member member : target.getMembers()) {
            table.addRow(rowFactory.create(member));
        }

        sender.send(table.render(ctx.getName(), page));

    }
}
