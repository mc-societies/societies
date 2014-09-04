package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a WorldExecutor
 */
public class WorldExecutor implements Executor<Sender> {

    private final List disabledWorlds;

    @Inject
    public WorldExecutor(@Named("blacklisted-worlds") ArrayList disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if (sender instanceof SocietyMember) {
            Player player = ((SocietyMember) sender).toPlayer();

            if (player == null) {
                return;
            }

            if (disabledWorlds.contains(player.getWorld().getName())) {
                ctx.cancel();
            }
        }
    }
}
