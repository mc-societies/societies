package org.societies.teleport;

import org.societies.api.math.Location;
import org.societies.groups.member.Member;

/**
 * Represents a Teleporter
 */
public interface Teleporter {

    void teleport(final Member member, final Location target);
}
