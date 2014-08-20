package net.catharos.societies;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a NameProvider
 */
public interface NameProvider {

    @Nullable
    String getName(UUID uuid);
}
