package net.catharos.societies.teleport;

import net.catharos.societies.bridge.Location;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a Teleporter
 */
public interface Teleporter {

    void teleport(final SocietyMember member, final Location target);
}
