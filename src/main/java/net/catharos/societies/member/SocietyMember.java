package net.catharos.societies.member;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.economy.EconomyParticipant;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 *
 */
public interface SocietyMember extends Member, Sender, EconomyParticipant {

    Locale getLocale();

    boolean hasPermission(String permission);

    @Nullable
    Player toPlayer();
}
