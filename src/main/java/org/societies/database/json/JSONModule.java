package org.societies.database.json;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.shank.service.AbstractServiceModule;
import org.societies.api.lock.DummyLocker;
import org.societies.api.lock.Locker;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.group.*;
import org.societies.groups.group.memory.MemoryGroupFactory;
import org.societies.groups.group.memory.MemoryGroupHeart;
import org.societies.groups.member.*;
import org.societies.groups.member.memory.MemoryMemberFactory;
import org.societies.groups.member.memory.MemoryMemberHeart;

import static com.google.inject.Key.get;

/**
 * Represents a MemberProviderModule
 */
public class JSONModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        Key<JSONProvider> provider = get(new TypeLiteral<JSONProvider>() {});

        bindService().to(provider);

        bind(MemberMapper.class);

        // Member provider
        bind(MemberProvider.class).to(provider);

        bind(GroupFactory.class).to(MemoryGroupFactory.class);
        bind(MemberFactory.class).to(MemoryMemberFactory.class);


        install(new FactoryModuleBuilder()
                .implement(MemoryMemberHeart.class, MemoryMemberHeart.class)
                .build(new TypeLiteral<ExtensionFactory<MemoryMemberHeart, Member>>() {}));

        install(new FactoryModuleBuilder()
                .implement(MemoryGroupHeart.class, MemoryGroupHeart.class)
                .build(new TypeLiteral<ExtensionFactory<MemoryGroupHeart, Group>>() {}));


        install(new FactoryModuleBuilder()
                .implement(GroupHeart.class, MemoryGroupHeart.class)
                .build(new TypeLiteral<ExtensionFactory<GroupHeart, Group>>() {}));

        install(new FactoryModuleBuilder()
                .implement(MemberHeart.class, MemoryMemberHeart.class)
                .build(new TypeLiteral<ExtensionFactory<MemberHeart, Member>>() {}));

        // Group provider
        bind(GroupProvider.class).to(provider);

        Key<JSONGroupPublisher> groupPublisher =
                get(new TypeLiteral<JSONGroupPublisher>() {});

        // Group Publishers
        bind(GroupPublisher.class).to(groupPublisher);
        bind(GroupDestructor.class).to(groupPublisher);

        // Member Publishers
        bind(MemberPublisher.class).to(provider);
        bind(MemberDestructor.class).to(provider);

        bind(Locker.class).to(DummyLocker.class);
    }


}
