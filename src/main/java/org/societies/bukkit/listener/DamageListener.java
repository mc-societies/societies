package org.societies.bukkit.listener;

import com.google.inject.Inject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;
import org.shank.config.ConfigSetting;
import org.societies.api.group.Society;
import org.societies.api.member.SocietyMember;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

import java.util.List;

/**
 * Represents a EntityListener
 */
class DamageListener implements Listener {

    private final MemberProvider provider;
    private final List<String> disabledWorlds;
    private final boolean globalFFForced;
    private final boolean saveCivilians;

    @Inject
    public DamageListener(MemberProvider provider,
                          @ConfigSetting("blacklisted-worlds") List<String> disabledWorlds,
                          @ConfigSetting("pvp.global-ff-forced") boolean globalFFForced,
                          @ConfigSetting("pvp.save-civilians") boolean saveCivilians) {
        this.provider = provider;
        this.disabledWorlds = disabledWorlds;
        this.globalFFForced = globalFFForced;
        this.saveCivilians = saveCivilians;
    }

    @Nullable
    public Player findAttacker(EntityDamageByEntityEvent event) {
        Player attackerPlayer = null;
        Entity attackerEntity = event.getDamager();

        if (attackerEntity instanceof Player) {
            attackerPlayer = (Player) attackerEntity;
        } else if (attackerEntity instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) attackerEntity).getShooter();

            if (shooter instanceof Player) {
                attackerPlayer = (Player) shooter;
            }
        }

        return attackerPlayer;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity victimEntity = event.getEntity();

        if (!(victimEntity instanceof Player)) {
            return;
        }

        Player victimPlayer = (Player) victimEntity;

        if (disabledWorlds.contains(victimPlayer.getWorld().getName())) {
            return;
        }


        Player attackerPlayer = findAttacker(event);

        if (attackerPlayer == null) {
            return;
        }

        Member attacker, victim;

        attacker = provider.getMember(attackerPlayer.getUniqueId());
        victim = provider.getMember(victimPlayer.getUniqueId());

        Group attackerGroup = attacker.getGroup();
        Group victimGroup = victim.getGroup();

        if (victimGroup != null && attackerGroup != null) {

            // ally clan, deny damage
            if (victimGroup.getRelation(attackerGroup).getType() == Relation.Type.ALLIED) {
                event.setCancelled(true);
            }

            // personal ff enabled, allow damage
            // skip if globalff is on
            // group ff enabled, allow damage

            Society victimSociety = victimGroup.get(Society.class);
            SocietyMember victimSocietyMember = victimGroup.get(SocietyMember.class);


            boolean personalFF = victimSocietyMember.isFriendlyFire();
            boolean groupFF = victimSociety.isFriendlyFire();
            if (globalFFForced || personalFF || groupFF) {
                return;
            }

            // same clan, deny damage

            if (victimGroup.equals(attackerGroup)) {
                event.setCancelled(true);
                return;
            }

        } else {
            // not part of a clan - check if safeCivilians is set
            // ignore setting if he has a specific permissions
            if (saveCivilians &&
                    !victimPlayer.hasPermission("simpleclans.ignore-safe-civilians")) {
                event.setCancelled(true);
            }
        }

    }
}
