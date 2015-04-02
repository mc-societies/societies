package org.societies.group;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.shank.AbstractModule;
import org.societies.groups.DefaultRelation;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.Relation;
import org.societies.groups.RelationFactory;
import org.societies.groups.group.Group;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.rank.StaticRank;
import org.societies.groups.rank.memory.MemoryRank;
import org.societies.groups.validate.NameValidator;
import org.societies.groups.validate.TagValidator;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

/**
 * Represents a SocietyModule
 */
public class SocietyModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Rank.class, MemoryRank.class)
                .implement(Rank.class, Names.named("static"), StaticRank.class)
                .build(RankFactory.class));

        install(new FactoryModuleBuilder()
                .implement(Relation.class, DefaultRelation.class)
                .build(RelationFactory.class));

        bind(NameValidator.class).to(SimpleNameValidator.class);
        bind(TagValidator.class).to(SimpleTagValidator.class);

        Multibinder<ExtensionRoller<Group>> extensions = newSetBinder(
                binder(),
                new TypeLiteral<ExtensionRoller<Group>>() {}
        );


        extensions.addBinding().to(SocietyRoller.class);
    }

}
