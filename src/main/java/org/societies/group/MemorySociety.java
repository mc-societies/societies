package org.societies.group;

import org.societies.api.group.Society;
import org.societies.bridge.Location;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupPublisher;

import java.util.Optional;

/**
 * Represents a MemorySociety
 */
public class MemorySociety implements Society {

    private final Group owner;
    private final GroupPublisher groupPublisher;

    private double balance;
    private boolean verified;
    private boolean ff;
    private Location home;

    public MemorySociety(Group owner, GroupPublisher groupPublisher) {
        this.owner = owner;
        this.groupPublisher = groupPublisher;
    }


    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;

        if (owner.linked()) {
            groupPublisher.publish(owner);
        }
    }

    @Override
    public boolean isVerified() {
        return verified;
    }

    @Override
    public void setVerified(boolean newState) {
        this.verified = newState;

        if (owner.linked()) {
            groupPublisher.publish(owner);
        }
    }

    @Override
    public boolean isFriendlyFire() {
        return ff;
    }

    @Override
    public void setFriendlyFire(boolean on) {
        this.ff = on;

        if (owner.linked()) {
            groupPublisher.publish(owner);
        }
    }

    @Override
    public void setHome(Location home) {
        this.home = home;

        if (owner.linked()) {
            groupPublisher.publish(owner);
        }
    }

    @Override
    public void removeHome() {
        this.home = null;

        if (owner.linked()) {
            groupPublisher.publish(owner);
        }
    }

    @Override
    public Optional<Location> getHome() {
        return Optional.ofNullable(home);
    }
}
