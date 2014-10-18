package net.catharos.societies.group;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import gnu.trove.set.hash.THashSet;
import net.catharos.groups.*;
import net.catharos.groups.rank.DefaultRank;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.rank.StaticRank;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.lib.shank.AbstractModule;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Rank.class, DefaultRank.class)
                .implement(Rank.class, Names.named("static"), StaticRank.class)
                .build(RankFactory.class));

        install(new FactoryModuleBuilder()
                .implement(Group.class, DefaultGroup.class)
                .build(GroupFactory.class));

        install(new FactoryModuleBuilder()
                .implement(Relation.class, DefaultRelation.class)
                .build(RelationFactory.class));


        bind(Group.class).to(DefaultGroup.class);

        bind(NameValidator.class).to(SimpleNameValidator.class);
        bind(TagValidator.class).to(SimpleTagValidator.class);
    }

    @Provides
    @Singleton
    public Set<Rank> provideDefaultRanks(RankFactory rankFactory) {
        //fixme add rules
        THashSet<Rank> ranks = new THashSet<Rank>();

        Rank leader = rankFactory.createStatic(UUID.randomUUID(), "Leader", 0);
        leader.addRule("trust");

        Rank member = rankFactory.createStatic(UUID.randomUUID(), "Member", 0);
        leader.addRule("vitals");

        ranks.add(leader);
        ranks.add(member);

        return ranks;
    }

}
