package org.societies.group.rule;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import gnu.trove.set.hash.THashSet;
import org.shank.AbstractModule;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a RuleModule
 */
public class RuleModule extends AbstractModule {

    private final Config config;

    public RuleModule(Config config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        addRule("*");
        addRule("invite");
        addRule("join");
        addRule("leave");
        addRule("vitals");
        addRule("roster");
        addRule("kick");
        addRule("coords");
        addRule("trust");
        addRule("untrust");
        addRule("tag");

        addRule("home.teleport");
        addRule("home.regroup");
        addRule("home.set");

        addRule("rank.assign");
        addRule("rank.create");
        addRule("rank.list");
        addRule("rank.remove");

        addRule("rank.rules.assign");
        addRule("rank.rules.list");
        addRule("rank.rules.remove");

        addRule("allies.list");
        addRule("allies.add");
        addRule("allies.remove");

        addRule("rivals.list");
        addRule("rivals.add");
        addRule("rivals.remove");

        addRule("vote.join");
        addRule("vote.allies");
        addRule("vote.rivals");

        addRule("leader");

        install(new DefaultRankModule("default-rank", config.getString("ranks.default")));
        install(new DefaultRankModule("normal-default-rank", config.getString("ranks.normal-default")));
        install(new DefaultRankModule("super-default-rank", config.getString("ranks.super-default")));
    }

    @Provides
    @Singleton
    @Named("predefined-ranks")
    public Set<Rank> provideDefaultRanks(RankFactory rankFactory) {
        THashSet<Rank> ranks = new THashSet<Rank>();

        List<? extends Config> objectList = config.getConfigList("ranks.predefined");

        for (Config object : objectList) {
            String name = object.getString("name");
            int priority = object.getInt("priority");
            UUID uuid = UUID.fromString(object.getString("uuid"));
            List<String> rules = object.getStringList("rules");

            Rank rank = rankFactory.createStatic(uuid, name, priority, rules);

            ranks.add(rank);
        }

        return Collections.unmodifiableSet(ranks);
    }

    private void addRule(String rule) {
        rules().addBinding().toInstance(rule);
    }

    public Multibinder<String> rules() {
        return Multibinder.newSetBinder(binder(), String.class);
    }
}
