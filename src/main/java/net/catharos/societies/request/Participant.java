package net.catharos.societies.request;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a Participant
 */
public interface Participant {

    @Nullable
    Request getActiveRequest();

    void setActiveRequest(Request activeRequest);

    boolean clearRequest();
}
