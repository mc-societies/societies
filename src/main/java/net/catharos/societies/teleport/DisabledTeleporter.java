package net.catharos.societies.teleport;

import net.catharos.bridge.Location;
import net.catharos.societies.api.member.SocietyMember;


/**
 * Represents a DisabledTeleporter
 */
public class DisabledTeleporter implements Teleporter {
    @Override
    public void teleport(SocietyMember member, Location target) {
        member.send("teleport.disabled");
    }
}
