package net.catharos.societies.member;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface SocietyMember extends Member, Sender {
    String getName();

    @Nullable
    Player toPlayer();
}
