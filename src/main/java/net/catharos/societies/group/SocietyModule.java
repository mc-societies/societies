package net.catharos.societies.group;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.*;
import net.catharos.groups.rank.DefaultRank;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.rank.RankFactory;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.lib.shank.AbstractModule;

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
    }
}
