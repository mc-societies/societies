package org.societies.member;

import org.societies.PlayerRuntimeException;

import java.util.UUID;

/**
 * Represents a MemberLoadingException
 */
public class MemberException extends PlayerRuntimeException {

    public MemberException(UUID player) {
        super(player);
    }

    public MemberException(UUID uuid, String message, Object... args) {
        super(uuid, message, args);
    }

    public MemberException(UUID uuid, Throwable cause, String message, Object... args) {
        super(uuid, cause, message, args);
    }

    public MemberException(UUID uuid, Throwable cause) {
        super(uuid, cause);
    }

    public MemberException(UUID uuid, Exception e, String format) {
        super(uuid, e, format);
    }
}
