package net.catharos.societies.commands.society.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.set", async = true)
public class SetHomeCommand implements Executor<SocietyMember> {

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
            sender.send("society.not.found");
            return;
        }

        if (location == null) {
            Player player = sender.toPlayer();
            assert player != null : "Player not available!";
            location = player.getLocation();
        }

        group.set(homeSetting, location);
    }
}
