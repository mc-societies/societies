package net.catharos.societies.teleport;

import net.catharos.societies.member.SocietyMember;
import org.bukkit.Location;

/**
 * Represents a DisabledTeleporter
 */
public class DisabledTeleporter implements Teleporter {
    @Override
    public void teleport(SocietyMember member, Location target) {
        member.send("teleport.disabled");
    }
}
