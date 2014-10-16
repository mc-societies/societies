package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
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
@Command(identifier = "command.profile")
@Permission("societies.profile")
public class ProfileCommand implements Executor<Sender> {

    @Option(name = "argument.target.society")
    Group target;

    private final DateTimeFormatter dateTimeFormatter;
    private final PeriodFormatter periodFormatter;

    @Inject
    public ProfileCommand(DateTimeFormatter dateTimeFormatter, PeriodFormatter periodFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.periodFormatter = periodFormatter;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        if (target == null && sender instanceof Member) {
            target = ((Member) sender).getGroup();
        }

        if (target == null) {
            sender.send("target-society.not-specified");
            return;
        }

        sender.send("Name: " + target.getName());
        sender.send("UUID: " + target.getUUID());
        Period period = new Interval(target.getLastActive(), DateTime.now()).toPeriod();

        sender.send("Last active: {0}", target.getLastActive().toString(dateTimeFormatter));
        sender.send("Inactive: {0}", periodFormatter.print(period));
        sender.send("Founded: {0}", target.getCreated().toString(dateTimeFormatter));
        sender.send("Members Online: ");
        sender.send("Allies");
        sender.send("Rivals");
    }
}
