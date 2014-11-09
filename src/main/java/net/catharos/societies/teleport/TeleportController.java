package net.catharos.societies.teleport;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.Group;
import net.catharos.societies.bridge.*;
import net.catharos.societies.member.SocietyMember;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Math.abs;

/**
 * Represents a TeleportTask
 */
@Singleton
public class TeleportController implements Runnable, Teleporter {

    private final THashSet<TeleportState> states = new THashSet<TeleportState>();
    private final int delay;
    private final boolean dropItems;
    private final Materials materials;
    private final Set<Material> whitelisted;
    private final Set<Material> blacklisted;

    @Inject
    public TeleportController(
            @Named("teleport.delay") int delay,
            @Named("teleport.drop-items") boolean dropItems,
            @Named("teleport.item-whitelist") ArrayList whitelisted,
            @Named("teleport.item-blacklist") ArrayList blacklisted,
            Materials materials) {
        this.delay = delay;
        this.dropItems = dropItems;
        this.materials = materials;

        this.whitelisted = toMaterialSet(whitelisted);
        this.blacklisted = toMaterialSet(blacklisted);
    }


    private Set<Material> toMaterialSet(Iterable<String> input) {
        THashSet<Material> materials = new THashSet<Material>();

        for (String value : input) {
            Material material = this.materials.getMaterial(value);

            if (material != null) {
                materials.add(material);
            }
        }

        return materials;
    }

    public boolean isLocationEqual(Location location1, Location location2, double fuzzy) {
        return abs(location1.getX() - location2.getX()) <= fuzzy &&
                abs(location1.getY() - location2.getY()) <= fuzzy &&
                abs(location1.getZ() - location2.getZ()) <= fuzzy;
    }

    @Override
    public void run() {
        for (Iterator<TeleportState> it = states.iterator(); it.hasNext(); ) {
            TeleportState state = it.next();

            SocietyMember member = state.getMember();
            Location target = state.getDestination();

            Group group = member.getGroup();

            if (group == null) {
                continue;
            }

            if (!member.isAvailable()) {
                return;
            }


            if (!isLocationEqual(member.getLocation(), state.getStartLocation(), 0.5)) {
                member.send("you.teleport-cancelled");
                it.remove();
                continue;
            }

            state.decrease();

            if (state.getCounter() > 0) {
                member.send("teleport.left", state.getCounter());
            } else {

                it.remove();

                int x = target.getRoundedX();
                int z = target.getRoundedZ();

                if (!member.hasPermission("simpleclans.mod.keep-items")) {
                    dropItems(member);
                }

                member.teleport(target);

                member.send("you.at-home", group.getName());
            }
        }
    }

    private void dropItems(SocietyMember player) {
        if (!dropItems) {
            return;
        }

        Inventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        for (ItemStack item : contents) {

            if (item == null) {
                continue;
            }

            Material materials = item.getType();

            boolean whitelist = true;

            if (whitelisted.isEmpty()) {
                whitelist = false;
            }


            if ((whitelist && whitelisted.contains(materials)) || (!whitelist && !blacklisted.contains(materials))) {
                player.getWorld().dropItem(player.getLocation(), item);
                inv.remove(item);
            }
        }
    }

    @Override
    public void teleport(final SocietyMember member, final Location target) {
        states.add(new TeleportState(member, target, member.getLocation(), delay));
    }
}
