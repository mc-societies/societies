package net.catharos.societies.commands.society;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Sender;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.PeriodFormatter;

import javax.inject.Provider;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.roster")
@Sender(Member.class)
public class RosterCommand implements Executor<Member> {

    private final Provider<Table> tableProvider;
    private final PeriodFormatter periodFormatter;

    @Option(name = "argument.page")
    int page;

    @Inject
    public RosterCommand(Provider<Table> tableProvider, PeriodFormatter periodFormatter) {
        this.tableProvider = tableProvider;
        this.periodFormatter = periodFormatter;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Table table = tableProvider.get();

        table.addRow("Name", "Rank", "Seen");

        for (Member member : group.getMembers()) {
            Rank rank = Iterables.getFirst(member.getRanks(), null);
            table.addRow(member.getName(), rank == null ? "None" : rank.getName(), periodFormatter
                    .print(new Interval(member.getLastActive(), DateTime.now()).toPeriod()));
        }

        sender.send(table.render(ctx.getName(), page));
    }
}
