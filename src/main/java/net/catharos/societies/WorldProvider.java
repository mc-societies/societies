package net.catharos.societies;

import org.bukkit.World;

/**
 * Represents a WorldProvider
 */
public interface WorldProvider {

    World getWorld(String name);

    World getDefaultWorld();
}
