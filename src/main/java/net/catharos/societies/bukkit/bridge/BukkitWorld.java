package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.ItemStack;
import net.catharos.societies.bridge.Location;
import net.catharos.societies.bridge.World;
import org.bukkit.Server;

import java.util.UUID;

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
    public UUID getUUID() {
        return world.getUID(); //fuck off bukkit!!
    }

    @Override
    public String getName() {
        return world.getName();
    }

    public static Location toLocation(org.bukkit.Location loc) {
        return new Location(new BukkitWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ());
    }

    public static org.bukkit.Location toBukkitLocation(Server server, Location loc) {
        return new org.bukkit.Location(server.getWorld(loc.getWorld().getUUID()), loc.getX(), loc.getY(), loc.getZ());
    }

}
