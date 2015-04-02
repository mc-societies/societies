package org.societies.commands.society.home;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.societies.bridge.Location;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.commands.VerifyStep;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.teleport.TeleportController;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.home")
@Permission("societies.home.teleport")
@Children({
        HomeCommand.SetHomeCommand.class,
        HomeCommand.RegroupCommand.class,
//        HomeCommand.RemoveHomeCommand.class
})
@Meta({@Entry(key = RuleStep.RULE, value = "home.teleport"), @Entry(key = VerifyStep.VERIFY)})
@Sender(Member.class)
public class HomeCommand implements Executor<Member> {

    private final TeleportController teleportController;

    @Inject
    public HomeCommand( TeleportController teleportController) {
        this.teleportController = teleportController;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Society society = group.get(Society.class);

        Optional<Location> location = society.getHome();

        if (!location.isPresent()) {
            sender.send("home.not-set");
            return;
        }

        teleportController.teleport(sender, location.get());
        sender.send("home.teleporting");
    }


    @Command(identifier = "command.home.remove", async = true)
    @Permission("societies.home.remove")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.remove"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class RemoveHomeCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Society society = group.get(Society.class);

            society.removeHome();
            sender.send("home.removed");
        }
    }

    @Command(identifier = "command.home.set")
    @Permission("societies.home.set")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.set"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class SetHomeCommand implements Executor<Member> {

        @Option(name = "argument.location")
        Location location;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            if (location == null) {
                location = sender.get(Player.class).getLocation();
            }

            Society society = group.get(Society.class);

            Optional<Location> currentHome = society.getHome();

            if (currentHome.isPresent()) {
                sender.send("home.already-set");
                return;
            }

            society.setHome(location);
            sender.send("home.set", location.getX(), location.getY(), location.getZ());
        }
    }


    @Command(identifier = "command.home.regroup")
    @Permission("societies.home.regroup")
    @Meta({@Entry(key = RuleStep.RULE, value = "home.regroup"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class RegroupCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Society society = group.get(Society.class);

            Optional<Location> optional = society.getHome();

            if (!optional.isPresent()) {
                sender.send("home.not-set");
                return;
            }

            Location location = optional.get();

            if (location instanceof Location.InvalidLocation) {
                sender.send("home.not-valid");
                return;
            }

            Set<Member> members = group.getMembers();

            for (Member member : members) {
                member.get(Player.class).teleport(location);
                sender.send("home.regrouped");
            }
        }
    }
}
