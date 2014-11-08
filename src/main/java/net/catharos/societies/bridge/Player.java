package net.catharos.societies.bridge;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a Player
 */
public interface Player {

    double getHealth();

    int getFoodLevel();

    @Nullable
    Location getLocation();

    World getWorld();

    boolean teleport(Location location);

    void sendBlockChange(Location location, Material material, byte b);

    Inventory getInventory();
}
