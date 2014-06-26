package net.catharos.societies.request;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import net.catharos.lib.core.util.CastSafe;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;

/**
 * Represents a SimpleRequest
 */
public class SimpleRequest implements Request {

    private final Involved involved;
    private final SettableFuture<SimpleRequestResult> future = SettableFuture.create();

    private final THashMap<Participant, Choice> results = new THashMap<Participant, Choice>();

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

    @Nullable
    public Choice getState(Participant participant) {
        if (!isInvolved(participant)) {
            return null;
        }

        Choice choice = results.get(participant);
        return choice == null ? Choices.ABSTAIN : choice;
    }

    @Override
    public void vote(Participant participant, Choice choice) {
        if (isInvolved(participant)) {
            results.put(participant, choice);
            check();
        }
    }

    public Map<Choice, Number> stats() {
        return CastSafe.toGeneric(internalStats());
    }

    private Map<Choice, MutableInt> internalStats() {
        final THashMap<Choice, MutableInt> stats = new THashMap<Choice, MutableInt>();

        results.forEachValue(new TObjectProcedure<Choice>() {
            @Override
            public boolean execute(Choice object) {
                MutableInt count = stats.get(object);

                if (count == null) {
                    stats.put(object, count = new MutableInt());
                }

                count.increment();
                return true;
            }
        });

        return stats;
    }

    private void check() {
        if (isPending()) {
            return;
        }

        Map<Choice, MutableInt> stats = internalStats();

        ArrayList<Map.Entry<Choice, MutableInt>> sorted = Lists.newArrayList(stats.entrySet());
        sort(sorted, reverseOrder(new EntryValueComparator<Choice, MutableInt>()));

        //Check for duplicates
        MutableInt last = null;
        for (Map.Entry<Choice, MutableInt> entry : sorted) {
            if (entry.getValue().equals(last)) {
                future.set(new SimpleRequestResult());
                return;
            }

            last = entry.getValue();
        }

        if (sorted.isEmpty()) {
            return;
        }

        future.set(new SimpleRequestResult(sorted.get(0).getKey(), this));
    }

    @Override
    public boolean isPending() {
        return results.size() < involved.getInvolved().size();
    }

    @Override
    public DateTime getDateCreated() {
        return created;
    }

    @Override
    public ListenableFuture<SimpleRequestResult> result() {
        return future;
    }

    public static enum Choices implements Choice {

        ACCEPT,
        DENY,
        ABSTAIN

    }

    private static class EntryValueComparator<K, V extends Comparable<V>> implements Comparator<Map.Entry<K, V>> {
        @Override
        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }
}
