package net.catharos.societies;

import net.catharos.societies.bridge.World;

/**
 * Represents a WorldProvider
 */
public interface WorldProvider {

    World getWorld(String name);

    World getDefaultWorld();
}
