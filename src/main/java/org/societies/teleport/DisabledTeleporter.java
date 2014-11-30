package org.societies.teleport;

import org.societies.bridge.Location;
import org.societies.groups.member.Member;


/**
 * Represents a DisabledTeleporter
 */
public class DisabledTeleporter implements Teleporter {
    @Override
    public void teleport(Member member, Location target) {
        member.send("teleport.disabled");
    }
}
