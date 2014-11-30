package org.societies.group;

import net.catharos.lib.core.lang.ArgumentRuntimeException;

/**
 * Represents a MemberLoadingException
 */
public class SocietyException extends ArgumentRuntimeException {

    public SocietyException() {
    }

    public SocietyException(String message, Object... args) {
        super(message, args);
    }

    public SocietyException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public SocietyException(Throwable cause) {
        super(cause);
    }
}
