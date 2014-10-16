package net.catharos.societies.commands.society.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.teleport.TeleportController;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.home")
@Sender(value = SocietyMember.class)
@Children({
        HomeCommand.SetHomeCommand.class,
        HomeCommand.RegroupCommand.class,
        HomeCommand.RemoveHomeCommand.class
})
@Meta(@Entry(key = RuleStep.RULE, value = "home"))
public class HomeCommand implements Executor<SocietyMember> {

    private final Setting<Location> homeSetting;

    private final TeleportController teleportController;

    @Inject
    public HomeCommand(@Named("home") Setting<Location> homeSetting, TeleportController teleportController) {
        this.homeSetting = homeSetting;
        this.teleportController = teleportController;
    }

    @Override
    public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        Location location = group.get(homeSetting);

        if (location == null) {
            sender.send("home.not-set");
            return;
        }

        teleportController.teleport(sender, location);
        sender.send("home.teleporting");
    }


    @Command(identifier = "command.home.remove", async = true)
    @Sender(value = SocietyMember.class)
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.remove"))
    public static class RemoveHomeCommand implements Executor<Member> {

        private final Setting<Location> homeSetting;

        @Inject
        public RemoveHomeCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            group.remove(homeSetting);
            sender.send("home.removed");
        }
    }

    @Command(identifier = "command.home.set")
    @Sender(value = SocietyMember.class)
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.set"))
    public static class SetHomeCommand implements Executor<SocietyMember> {

        @Option(name = "argument.location")
        Location location;

        private final Setting<Location> homeSetting;

        @Inject
        public SetHomeCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            if (location == null) {
                Player player = sender.toPlayer();
                assert player != null : "Player not available!";
                location = player.getLocation();
            }

            group.set(homeSetting, location);
            sender.send("home.set", location.getX(), location.getY(), location.getZ());
        }
    }


    @Command(identifier = "command.home.regroup")
    @Sender(value = SocietyMember.class)
    @Meta(@Entry(key = RuleStep.RULE, value = "rank.regroup"))
    public static class RegroupCommand implements Executor<Member> {

        private final Setting<Location> homeSetting;

        @Inject
        public RegroupCommand(@Named("home") Setting<Location> homeSetting) {
            this.homeSetting = homeSetting;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Location location = group.get(homeSetting);

            if (location == null) {
                sender.send("home.not-set");
                return;
            }

            //fixme teleport all
        }
    }
}
