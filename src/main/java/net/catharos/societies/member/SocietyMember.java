package net.catharos.societies.member;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 *
 */
public interface SocietyMember extends Member, Sender {

    Locale getLocale();

    boolean hasPermission(String permission);

    @Nullable
    Player toPlayer();
}
