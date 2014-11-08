package net.catharos.societies.bridge;

/**
 *
 */
public interface World {

    void dropItem(Location location, ItemStack itemStack);

    String getName();
}
