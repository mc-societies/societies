package org.societies.group.rule;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.societies.groups.rank.Rank;

import java.util.Set;

/**
 * Represents a DefaultRankProvider
 */
class DefaultRankProvider implements Provider<Rank> {

    private final RankSelector selector;
    private final Set<Rank> ranks;

    @Inject
    private DefaultRankProvider(RankSelector selector, @Named("predefined-ranks") Set<Rank> ranks) {
        this.selector = selector;
        this.ranks = ranks;
    }

    @Override
    public Rank get() {
        for (Rank rank : ranks) {
            if (selector.is(rank)) {
                return rank;
            }
        }

        throw new RuntimeException("Failed to find rank " + selector.getName() + "!");
    }
}
