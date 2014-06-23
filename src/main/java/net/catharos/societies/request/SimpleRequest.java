package net.catharos.societies.request;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.lang3.mutable.MutableInt;
import org.joda.time.DateTime;

import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a SimpleRequest
 */
public class SimpleRequest implements Request {

    private final Involved involved;

    private final THashSet<Participant> participants = new THashSet<Participant>();
    private final TreeMap<Choice, MutableInt> choices = new TreeMap<Choice, MutableInt>();

    private final SettableFuture<Choice> future = SettableFuture.create();

    private final DateTime created;

    public SimpleRequest(Involved delegate) {
        this.involved = delegate;
        this.created = DateTime.now();
    }

    @Override
    public boolean isInvolved(Participant participant) {
        return involved.isInvolved(participant);
    }

    @Override
    public Set<Participant> getInvolved() {
        return involved.getInvolved();
    }

//    @Nullable
//    public Choice getState(Participant participant) {
//        if (!isInvolved(participant)) {
//            return null;
//        }
//
//        Choice choice = choices.get(participant);
//        return choice == null ? Choices.ABSTAIN : choice;
//    }

    @Override
    public void vote(Participant participant, Choice choice) {
        if (!participants.contains(participant) && isInvolved(participant)) {
            participants.add(participant);
            count(choice);
        }
    }

    private void count(Choice choice) {
        MutableInt count = choices.get(choice);

        if (count == null) {
            choices.put(choice, count = new MutableInt());
        }

        count.increment();

        if (!isPending()) {
            future.set(choices.lastKey());
        }
    }

    @Override
    public boolean isPending() {
        return participants.size() < involved.getInvolved().size();
    }

    @Override
    public DateTime getDateCreated() {
        return created;
    }

    @Override
    public ListenableFuture<Choice> result() {
        return future;
    }

    public static enum Choices implements Choice {

        ACCEPT,
        DENY,
        ABSTAIN

    }
}
