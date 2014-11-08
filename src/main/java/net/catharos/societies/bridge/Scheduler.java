package net.catharos.societies.bridge;

/**
 * Represents a Scheduler
 */
public interface Scheduler {

    void scheduleSyncRepeatingTask(Runnable task, long delay, long period);
}
