package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.ItemStack;
import net.catharos.societies.bridge.Location;
import net.catharos.societies.bridge.World;

/**
 * Represents a BukkitWorld
 */
public class BukkitWorld implements World {

    private final org.bukkit.World world;

    public BukkitWorld(org.bukkit.World world) {this.world = world;}

    @Override
    public void dropItem(Location location, ItemStack itemStack) {
        world.dropItemNaturally(
                new org.bukkit.Location(world, location.getX(), location.getY(), location.getZ()),
                BukkitItemStack.toBukkitItemStack(itemStack)
        );
    }

    @Override
    public String getName() {
        return world.getName();
    }

    public static Location toLocation(org.bukkit.Location loc) {
        return new Location(new BukkitWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ());
    }
}
