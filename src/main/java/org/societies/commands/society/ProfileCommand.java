package org.societies.commands.society;

import com.google.inject.Inject;
import order.CommandContext;
import order.Executor;
import order.reflect.Command;
import order.reflect.Option;
import order.reflect.Permission;
import order.sender.Sender;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.societies.api.Groups;
import org.societies.bridge.Player;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

/**
 * Represents a SocietyProfile
 */
@Command(identifier = "command.profile", async = true)
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

        Period inactivePeriod = new Interval(target.getLastActive(), DateTime.now()).toPeriod();

        sender.send("profile.name", target.getName());
        sender.send("profile.tag", target.getTag());
        sender.send("profile.last-active", target.getLastActive().toString(dateTimeFormatter));
        sender.send("profile.inactive", Groups.isActive(target.getMembers()) ? ":profile.active" : periodFormatter
                .print(inactivePeriod));
        sender.send("profile.founded", target.getCreated().toString(dateTimeFormatter));
        sender.send("profile.members");

        for (Member member : target.getMembers()) {
            if (member.get(Player.class).isAvailable()) {
                sender.send("profile.member-format", member.getName());
            }
        }
    }
}
