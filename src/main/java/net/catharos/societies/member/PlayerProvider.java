package net.catharos.societies.member;

import java.util.UUID;

/**
 * Represents a PlayerProvider
 */
public interface PlayerProvider<P> {

    P getPlayer(String name);

    P getPlayer(UUID uuid);
}
