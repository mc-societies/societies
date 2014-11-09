package net.catharos.societies.bridge;

/**
 * Represents a Player
 */
public interface Player {

    double getHealth();

    int getFoodLevel();

    Location getLocation();

    World getWorld();

    boolean teleport(Location location);

    void sendBlockChange(Location location, Material material, byte b);

    Inventory getInventory();
}
