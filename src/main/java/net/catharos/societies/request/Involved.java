package net.catharos.societies.request;

import java.util.Set;

/**
 * Represents a Action
 */
public interface Involved {

    boolean isInvolved(Participant participant);

    Set<Participant> getInvolved();
}
