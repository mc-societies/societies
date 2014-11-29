package net.catharos.societies.teleport;

import net.catharos.bridge.Location;
import net.catharos.groups.Member;

/**
 * Represents a Teleporter
 */
public interface Teleporter {

    void teleport(final Member member, final Location target);
}
