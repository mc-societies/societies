package org.societies.group.rule;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.societies.groups.rank.Rank;

/**
 * Represents a DefaultRankModule
 */
class DefaultRankModule extends PrivateModule {

    private final String keyName;
    private final String defaultRank;

    DefaultRankModule(String keyName, String defaultRank) {
        this.keyName = keyName;
        this.defaultRank = defaultRank;
    }

    @Override
    protected void configure() {
        Key<Rank> key = Key.get(Rank.class, Names.named(keyName));

        bind(RankSelector.class).toInstance(new RankSelector() {
            @Override
            public String getName() {
                return defaultRank;
            }

            @Override
            public boolean is(Rank rank) {
                return rank.getName().equals(defaultRank);
            }
        });

        bind(key).toProvider(DefaultRankProvider.class).in(Singleton.class);
        expose(key);
    }
}
