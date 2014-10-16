package net.catharos.societies.teleport;

import net.catharos.societies.member.SocietyMember;
import org.bukkit.Location;

/**
 * Represents a Teleporter
 */
public interface Teleporter {

    void teleport(final SocietyMember member, final Location target);
}
