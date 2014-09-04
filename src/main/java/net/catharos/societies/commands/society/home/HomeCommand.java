package net.catharos.societies.commands.society.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.teleport.TeleportController;
import org.bukkit.Location;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.home", async = false)
@Sender(sender = SocietyMember.class)
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
    }
}
