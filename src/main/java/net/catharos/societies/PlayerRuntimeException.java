package net.catharos.societies;

import net.catharos.lib.core.lang.ArgumentRuntimeException;

import java.util.UUID;

/**
 * This exception indicates that something badly happened because of a player. So let this player be punished!
 */
public class PlayerRuntimeException extends ArgumentRuntimeException {
    /** The player name */
    private final UUID player;

    /** Indicated if the player should be kicked */
    private boolean kick = true;


    public PlayerRuntimeException(UUID player) {
        super();

        this.player = player;
    }

    public PlayerRuntimeException(UUID uuid, String message, Object... args) {
        super(message, args);

        this.player = uuid;
    }

    public PlayerRuntimeException(UUID uuid, Throwable cause, String message, Object... args) {
        super(cause, message, args);

        this.player = uuid;
    }

    public PlayerRuntimeException(UUID uuid, Throwable cause) {
        super(cause);

        this.player = uuid;
    }

    public PlayerRuntimeException(UUID uuid, Exception e, String format) {
        super(e, format);

        this.player = uuid;
    }

    public PlayerRuntimeException dontKick() {
        kick = false;

        return this;
    }

    /**
     * Returns the player name.
     *
     * @return The name
     */
    public UUID getPlayer() {
        return player;
    }

    /**
     * Returns whether or not the specified player should be kicked from the server.
     *
     * @return True if he should get kicked, false if not
     */
    public boolean shouldKick() {
        return kick;
    }

}
