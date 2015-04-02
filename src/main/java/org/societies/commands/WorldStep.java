package org.societies.commands;

import com.google.inject.Inject;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.sender.Sender;
import org.shank.config.ConfigSetting;
import org.societies.bridge.Player;
import org.societies.groups.member.Member;

import java.util.List;

/**
 * Represents a WorldExecutor
 */
public class WorldStep implements Executor<Sender> {

    private final List<String> disabledWorlds;

    @Inject
    public WorldStep(@ConfigSetting("blacklisted-worlds") List<String> disabledWorlds) {
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
