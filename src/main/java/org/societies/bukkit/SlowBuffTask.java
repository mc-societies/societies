package org.societies.bukkit;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.societies.api.math.Location;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.SiegeController;
import org.societies.groups.member.MemberProvider;


/**
 * Represents a SlowBuffTask
 */
public class SlowBuffTask implements Runnable {

    private final SiegeController siegeController;
    private final MemberProvider memberProvider;

    @Inject
    public SlowBuffTask(SiegeController siegeController, MemberProvider memberProvider) {
        this.siegeController = siegeController;
        this.memberProvider = memberProvider;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = new Location(player.getLocation());

            applyBuff(player, location, siegeController.getSiegeByLocation(location));

            applyBuff(player, location, siegeController.getSiegeInitiatedNear(location));
        }
    }

    public void applyBuff(Player player, Location location, Optional<Siege> optional) {
        if (optional.isPresent()) {
            Siege siege = optional.get();


            if (!siege.getBesieger().getGroup().getMembers().contains(memberProvider.getMember(player.getUniqueId()))) {
                return;
            }

            City city = siege.getCity();
            if (city.getLocation().distance(location) < 5) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 80, 1));
            }
        }
    }
}
