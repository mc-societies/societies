package net.catharos.societies;

import java.util.UUID;

/**
 * Represents a NameProvider
 */
public interface NameProvider {

    String getName(UUID uuid);
}
