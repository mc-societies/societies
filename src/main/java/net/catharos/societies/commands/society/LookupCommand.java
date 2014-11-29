package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.bridge.Player;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.lookup", async = true)
@Permission("societies.lookup")
public class LookupCommand implements Executor<Sender> {

    @Option(name = "argument.target.member")
    Member target;

    private final DateTimeFormatter dateTimeFormatter;
    private final PeriodFormatter periodFormatter;

    @Inject
    public LookupCommand(DateTimeFormatter dateTimeFormatter, PeriodFormatter periodFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.periodFormatter = periodFormatter;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender);
        }

        if (target == null) {
            sender.send("target.member.not.specified");
            return;
        }

        Group group = target.getGroup();
        Period inactive = new Interval(target.getLastActive(), DateTime.now()).toPeriod();

        sender.send("lookup.name", target.getName());
        if (group != null) {
            sender.send("lookup.society", group.getName());
            sender.send("lookup.society-tag", group.getTag());
        }
        Rank defaultRank = target.getRank();
        if (defaultRank != null) {
            sender.send("lookup.rank", defaultRank.getName());
        }
        sender.send("lookup.uuid", target.getUUID());
        sender.send("lookup.join-date", target.getCreated().toString(dateTimeFormatter));
        sender.send("lookup.last-seen", target.getLastActive().toString(dateTimeFormatter));
        sender.send("lookup.inactive", target.get(Player.class)
                .isAvailable() ? ":lookup.online" : periodFormatter.print(inactive));
    }
}
