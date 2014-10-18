package net.catharos.societies.group;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import net.catharos.groups.*;
import net.catharos.groups.rank.DefaultRank;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.rank.StaticRankProvider;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.lib.shank.AbstractModule;

import java.util.UUID;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Rank.class, DefaultRank.class)
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


        //fixme add rules
        defaultRanks().addBinding().toInstance(new StaticRankProvider(UUID.randomUUID(), "Leader", 0));
        defaultRanks().addBinding().toInstance(new StaticRankProvider(UUID.randomUUID(), "Member", 0));
    }

    public Multibinder<StaticRankProvider> defaultRanks() {
        return Multibinder
                .newSetBinder(binder(), new TypeLiteral<StaticRankProvider>() {}, Names.named("default-ranks"))
                .permitDuplicates();
    }

}
