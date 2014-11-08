package net.catharos.societies.member;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.bridge.Player;
import net.catharos.societies.economy.EconomyParticipant;

import java.util.Locale;

/**
 *
 */
public interface SocietyMember extends Member, Sender, EconomyParticipant, Player {

    Locale getLocale();

    boolean hasPermission(String permission);
}
