package net.catharos.societies.commands.society;

import com.google.inject.Inject;
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
@Command(identifier = "command.lookup")
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

        sender.send("Name: {0}", target.getName());
        Group group = target.getGroup();
        if (group != null) {
            sender.send("Society: {0}", target.getGroup().getName());
        }
        sender.send("UUID: {0}", target.getUUID());

        Period period = new Interval(target.getLastActive(), DateTime.now()).toPeriod();

        sender.send("Join Date: {0}", target.getCreated().toString(dateTimeFormatter));
        sender.send("Last Seen: {0}", target.getLastActive().toString(dateTimeFormatter));
        sender.send("Inactive: {0}", periodFormatter.print(period));

        sender.send("Ranks:");
        for (Rank rank : target.getRanks()) {
            sender.send(" -" + rank.getName());
        }

    }
}
