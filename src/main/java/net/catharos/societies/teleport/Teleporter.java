package net.catharos.societies.teleport;

import net.catharos.bridge.Location;
import net.catharos.societies.api.member.SocietyMember;

/**
 * Represents a Teleporter
 */
public interface Teleporter {

    void teleport(final SocietyMember member, final Location target);
}
