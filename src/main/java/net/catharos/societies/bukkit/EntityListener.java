package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.Relation;
import net.catharos.groups.setting.Setting;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Represents a EntityListener
 */
public class EntityListener implements Listener {

    private final MemberProvider<SocietyMember> provider;
    private final ArrayList disabledWorlds;
    private final boolean globalFFForced;
    private final boolean saveCivilians;
    private final Setting<Boolean> personalFF;
    private final Setting<Boolean> groupFF;

    @Inject
    public EntityListener(MemberProvider<SocietyMember> provider,
                          @Named("blacklisted-worlds") ArrayList disabledWorlds,
                          @Named("pvp.global-ff-forced") boolean globalFFForced,
                          @Named("pvp.save-civilians") boolean saveCivilians,
                          @Named("personal-friendly-fire") Setting<Boolean> personalFF,
                          @Named("group-friendly-fire") Setting<Boolean> groupFF) {
        this.provider = provider;
        this.disabledWorlds = disabledWorlds;
        this.globalFFForced = globalFFForced;
        this.saveCivilians = saveCivilians;

        this.personalFF = personalFF;
        this.groupFF = groupFF;
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

        if (disabledWorlds.contains(victimPlayer.getWorld())) {
            return;
        }


        Player attackerPlayer = findAttacker(event);

        if (attackerPlayer == null) {
            return;
        }

        Member attacker, victim;

        try {
            attacker = provider.getMember(attackerPlayer.getUniqueId()).get();
            victim = provider.getMember(victimPlayer.getUniqueId()).get();
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }

        Group attackerGroup = attacker.getGroup();
        Group victimGroup = victim.getGroup();

        if (victimGroup != null && attackerGroup != null) {
            // personal ff enabled, allow damage
            // skip if globalff is on
            // group ff enabled, allow damage

            if (globalFFForced || victim.get(personalFF) || victimGroup.get(groupFF)) {
                return;
            }

            // same clan, deny damage

            if (victimGroup.equals(attackerGroup)) {
                event.setCancelled(true);
                return;
            }

            // ally clan, deny damage

            if (victimGroup.getRelation(attackerGroup).getType() == Relation.Type.ALLIED) {
                event.setCancelled(true);
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
