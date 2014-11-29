package net.catharos.societies.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.bridge.Player;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a WorldExecutor
 */
public class WorldStep implements Executor<Sender> {

    private final List disabledWorlds;

    @Inject
    public WorldStep(@Named("blacklisted-worlds") ArrayList disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if (sender instanceof Member) {
            Member member = ((Member) sender);

            Player player = member.get(Player.class);
            if (player.isAvailable()) {
                return;
            }

            if (disabledWorlds.contains(player.getLocation().getWorld().getName())) {
                ctx.cancel();
            }
        }
    }
}
