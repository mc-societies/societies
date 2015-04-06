package org.societies.bukkit;

import com.google.inject.Inject;
import order.sender.Sender;
import org.bukkit.entity.Player;
import org.societies.api.economy.EconomyParticipant;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.member.Member;

import java.util.UUID;

/**
 * Represents a BukkitExtensionRoller
 */
class BukkitExtensionRoller implements ExtensionRoller<Member> {

    private final ExtensionFactory<BukkitSocietiesMember, UUID> bukkitFactory;

    @Inject
    private BukkitExtensionRoller(ExtensionFactory<BukkitSocietiesMember, UUID> bukkitFactory) {
        this.bukkitFactory = bukkitFactory;
    }

    @Override
    public void roll(Member extensible) {
        extensible.add(Player.class, new DelegatingPlayer(extensible.getUUID()));

        BukkitSocietiesMember bukkit = bukkitFactory.create(extensible.getUUID());
        extensible.add(EconomyParticipant.class, bukkit);
        extensible.add(Sender.class, bukkit);
    }
}
