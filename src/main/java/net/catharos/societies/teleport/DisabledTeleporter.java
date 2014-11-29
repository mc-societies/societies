package net.catharos.societies.teleport;

import net.catharos.bridge.Location;
import net.catharos.groups.Member;


/**
 * Represents a DisabledTeleporter
 */
public class DisabledTeleporter implements Teleporter {
    @Override
    public void teleport(Member member, Location target) {
        member.send("teleport.disabled");
    }
}
