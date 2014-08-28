package net.catharos.societies.commands.society.home;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.setting.Setting;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import org.bukkit.Location;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.home.remove", async = true)
public class RemoveHomeCommand implements Executor<Member> {

    private final Setting<Location> homeSetting;

    @Inject
    public RemoveHomeCommand(@Named("home") Setting<Location> homeSetting) {
        this.homeSetting = homeSetting;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not.found");
            return;
        }

        group.remove(homeSetting);
    }
}
