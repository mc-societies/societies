package net.catharos.societies.request;

import net.catharos.lib.core.lang.ArgumentException;

/**
 * Represents a RequestFailedException
 */
public class RequestFailedException extends ArgumentException {

    public RequestFailedException() {
    }

    public RequestFailedException(String message, Object... args) {
        super(message, args);
    }

    public RequestFailedException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }

    public RequestFailedException(Throwable cause) {
        super(cause);
    }
}
