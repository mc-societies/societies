package net.catharos.societies;

import java.util.UUID;

/**
 * Represents a PlayerProvider
 */
public interface PlayerProvider {

    UUID getPlayer(String name);

    boolean isAvailable(String name);

    boolean isAvailable(UUID uuid);

}
