package org.societies.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.shank.service.AbstractServiceModule;
import org.societies.database.QueryProvider;
import org.societies.group.OnlineGroupCache;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.cache.NaughtyGroupCache;
import org.societies.groups.group.*;
import org.societies.groups.member.*;
import org.societies.lock.sql.SQLLockModule;
import org.societies.member.OnlineMemberCache;

import static com.google.inject.Key.get;

/**
 * Represents a SQLModule
 */
public class SQLModule extends AbstractServiceModule {

    private final boolean cache;

    public SQLModule(boolean cache) {
        this.cache = cache;
    }

    @Override
    protected void configure() {
        bind(Queries.class);

        bindNamed("main", QueryProvider.class).to(Queries.class);

        Key<SQLProvider> controller = get(SQLProvider.class);

        // Member provider
        bind(MemberCache.class).to(OnlineMemberCache.class);

        bind(MemberProvider.class).to(controller);

        if (cache) {
            bind(GroupCache.class).to(OnlineGroupCache.class);

            install(new FactoryModuleBuilder()
                    .implement(MemberHeart.class, SQLLazyMemberHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLMemberHeart, Member>>() {}));

            install(new FactoryModuleBuilder()
                    .implement(GroupHeart.class, SQLLazyGroupHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLGroupHeart, Group>>() {}));
        } else {
            install(new FactoryModuleBuilder()
                    .implement(MemberHeart.class, SQLMemberHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLMemberHeart, Member>>() {}));

            install(new FactoryModuleBuilder()
                    .implement(GroupHeart.class, SQLGroupHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLGroupHeart, Group>>() {}));

            bind(GroupCache.class).to(NaughtyGroupCache.class);
        }


        bind(GroupFactory.class).to(SQLGroupFactory.class);

        bind(MemberFactory.class).to(SQLMemberFactory.class);

        bind(GroupProvider.class).to(controller);

        // Group Publishers
        bind(GroupPublisher.class).to(SQLGroupPublisher.class);
        bind(GroupDestructor.class).to(SQLGroupDestructor.class);

        // Member Publishers
        bind(MemberPublisher.class).to(SQLMemberPublisher.class);

        install(new SQLLockModule());

        bindService().to(RankService.class);
    }
}
