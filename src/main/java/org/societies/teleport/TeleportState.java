package org.societies.teleport;

import org.societies.api.math.Location;
import org.societies.groups.member.Member;

/**
 * Represents a TeleportState
 */
class TeleportState {

    private final Member member;
    private final Location destination;

    private final Location startLocation;

    private int counter;

    public TeleportState(Member member, Location destination, Location startLocation, int counter) {
        this.member = member;
        this.destination = destination;
        this.startLocation = startLocation;
        this.counter = counter;
    }

    public void increase() {
        this.counter++;
    }

    public void decrease() {
        this.counter--;
    }

    public Member getMember() {
        return member;
    }

    public Location getDestination() {
        return destination;
    }

    public int getCounter() {
        return counter;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeleportState that = (TeleportState) o;

        return member.equals(that.member);
    }

    @Override
    public int hashCode() {
        return member.hashCode();
    }
}
