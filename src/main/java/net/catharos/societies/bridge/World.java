package net.catharos.societies.bridge;

import java.util.UUID;

/**
 *
 */
public interface World {

    void dropItem(Location location, ItemStack itemStack);

    UUID getUUID();

    String getName();
}
