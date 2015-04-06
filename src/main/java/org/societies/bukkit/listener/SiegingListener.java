package org.societies.bukkit.listener;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.societies.api.sieging.*;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

import java.util.Set;

/**
 * Represents a SiegingListener
 */
class SiegingListener implements Listener {

    private final ActionValidator actionValidator;
    private final MemberProvider memberProvider;
    private final CityProvider cityProvider;
    private final SiegeController siegeController;

    private final Server server;

    @Inject
    public SiegingListener(ActionValidator actionValidator,
                           MemberProvider memberProvider, CityProvider cityProvider,
                           SiegeController siegeController,
                           Server server) {
        this.actionValidator = actionValidator;
        this.memberProvider = memberProvider;
        this.cityProvider = cityProvider;
        this.siegeController = siegeController;
        this.server = server;
    }

    private Member getMember(Player player) {
        return memberProvider.getMember(player.getUniqueId());
    }

    private Besieger getBesieger(Member member) {
        Group group = member.getGroup();

        return group == null ? null : group.get(Besieger.class);
    }

    private Besieger getBesieger(Player player) {
        Member member = getMember(player);
        return getBesieger(member);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        boolean can = can(ActionValidator.Action.DESTROY, event.getPlayer(), block.getLocation());

        if (!can) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        boolean can = can(ActionValidator.Action.BUILD, event.getPlayer(), block.getLocation());

        if (!can) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block != null) {
            boolean can = can(ActionValidator.Action.INTERACT, event.getPlayer(), block.getLocation());

            if (!can) {
                event.setCancelled(true);
            }
        }
    }


    private boolean can(int action, Player player, Location location) {
        Besieger besieger = getBesieger(player);

        return actionValidator.can(action, besieger, new org.societies.api.math.Location(location));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkBindstoneBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        Besieger besieger = getBesieger(event.getPlayer());

        if (besieger == null) {
            return;
        }

        Optional<City> optional = cityProvider.getCity(new org.societies.api.math.Location(blockLocation));

        if (!optional.isPresent()) {
            return;
        }

        City city = optional.get();
        org.societies.api.math.Location bindstone = city.getLocation();

        if (bindstone.getRoundedX() == blockLocation.getBlockX()
                && bindstone.getRoundedY() == blockLocation.getBlockY()
                && bindstone.getRoundedZ() == blockLocation.getBlockZ()) {

            Set<Siege> sieges = siegeController.getSiegesByLocation(city);

            for (Siege siege : sieges) {
                if (siege.isStarted() && siege.getBesieger().equals(besieger)) {
                    siegeController.stop(siege, besieger);
                    Group owner = city.getOwner().getGroup();
                    siege.send("siege.besieger-won", siege.getBesieger().getGroup().getName(), city.getName(), owner.getName());
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkSiegestoneBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        Besieger besieger = getBesieger(event.getPlayer());

        if (besieger == null) {
            return;
        }

        Optional<Siege> optional = siegeController.getSiegeInitiatedAt(new org.societies.api.math.Location(blockLocation));

        if (!optional.isPresent()) {
            return;
        }


        Siege siege = optional.get();
        City city = siege.getCity();
        Besieger owner = city.getOwner();

        if (!owner.equals(besieger)) {
            event.setCancelled(true);
            return;
        }


        siegeController.stop(siege, owner);
        siege.send("siege.city-won", city.getName(), owner.getGroup().getName(), siege.getBesieger().getGroup().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void modifyPlayerRespawn(PlayerRespawnEvent event) {
        Member member = getMember(event.getPlayer());
        Besieger besieger = getBesieger(member);

        if (besieger != null) {
            Optional<Siege> siege = siegeController.getSiegeByAttacker(besieger);

            if (!siege.isPresent()) {
                return;
            }

            if (siege.get().isStarted()) {
                event.setRespawnLocation(siege.get().getLocationInitiated().toBukkit());
                member.send("siege.respawn-siegestone");
            }
        }
    }
}
