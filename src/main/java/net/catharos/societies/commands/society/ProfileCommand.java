package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
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

import java.util.ArrayList;
import java.util.Collection;

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
    private final GroupProvider groupProvider;

    @Inject
    public ProfileCommand(DateTimeFormatter dateTimeFormatter, PeriodFormatter periodFormatter, GroupProvider groupProvider) {
        this.dateTimeFormatter = dateTimeFormatter;
        this.periodFormatter = periodFormatter;
        this.groupProvider = groupProvider;
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
        Collection<Relation> relations = target.getRelations();
        ArrayList<Relation> rivals = new ArrayList<Relation>(relations.size());

        sender.send("profile.name", target.getName());
        sender.send("profile.uuid", target.getUUID());
        sender.send("profile.last-active", target.getLastActive().toString(dateTimeFormatter));
        sender.send("profile.inactive", periodFormatter.print(inactivePeriod));
        sender.send("profile.founded", target.getCreated().toString(dateTimeFormatter));
        sender.send("profile.members");

        for (Member member : target.getMembers()) {
            if (member.isAvailable()) {
                sender.send("profile.member-format", member.getName());
            }
        }

        sender.send("profile.allies");
        for (Relation relation : relations) {
            if (relation.getType() == Relation.Type.ALLIED) {
                sender.send("profile.ally-format", groupProvider.getGroup(relation.getOpposite(target.getUUID())));
            } else {
                rivals.add(relation);
            }
        }

        sender.send("profile.rivals");
        for (Relation relation : rivals) {
            sender.send("profile.rival-format", groupProvider.getGroup(relation.getOpposite(target.getUUID())));
        }
    }
}
