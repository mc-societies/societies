package org.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.jooq.SQLDialect;
import org.shank.service.AbstractServiceModule;
import org.societies.api.lock.Locker;
import org.societies.database.DSLProvider;
import org.societies.database.Database;
import org.societies.database.RemoteDatabase;
import org.societies.database.data.queue.DefaultQueue;
import org.societies.database.data.queue.Queue;
import org.societies.group.OnlineGroupCache;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.cache.NaughtyGroupCache;
import org.societies.groups.group.*;
import org.societies.groups.member.*;
import org.societies.groups.rank.RankPublisher;
import org.societies.member.OnlineMemberCache;

import java.util.concurrent.TimeUnit;

import static com.google.inject.Key.get;

/**
 * Represents a MemberProviderModule
 */
public class SQLModule extends AbstractServiceModule {

    private final boolean cache;

    public SQLModule(boolean cache) {this.cache = cache;}

    @Override
    protected void configure() {
        bindService().to(MigrationService.class);
        bindService().to(CleanupService.class);
        bindService().to(RankService.class);

        bind(Database.class).to(RemoteDatabase.class);
        bind(DSLProvider.class).to(RemoteDatabase.class);
        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        bind(SQLDialect.class).toInstance(SQLDialect.MYSQL);


//        bind(Database.class).to(URLDatabase.class);
//        bind(DSLProvider.class).to(URLDatabase.class);
//        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "org.h2.jdbcx.JdbcDataSource");
//        bindNamedString("db-url", "jdbc:h2:./test");
//        bindNamedString("db-driver", "org.h2.Driver");
//
//        bind(SQLDialect.class).toInstance(SQLDialect.H2);


        bind(Queue.class).to(DefaultQueue.class);
        bindNamedInstance("auto-flush-interval", long.class, 5000L);
        bindNamedInstance("max-batch-idle", long.class, 5000L);
        bindNamedInstance("queue-time-unit", TimeUnit.class, TimeUnit.MILLISECONDS);
        bindNamedInstance("critical-batch-size", int.class, 100);





        bind(Queries.class);

        Key<SQLProvider> controller = get(SQLProvider.class);

        // Member provider
        bind(MemberCache.class).to(OnlineMemberCache.class);

        bind(new TypeLiteral<MemberProvider>() {})
                .to(controller);

        if (cache) {
            bind(GroupCache.class).to(OnlineGroupCache.class);

            install(new FactoryModuleBuilder()
                    .implement(MemberHeart.class, SQLLazyMemberHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLMemberHeart, Member>>() {}));
        } else {
            install(new FactoryModuleBuilder()
                    .implement(MemberHeart.class, SQLMemberHeart.class)
                    .build(new TypeLiteral<ExtensionFactory<SQLMemberHeart, Member>>() {}));

            bind(GroupCache.class).to(NaughtyGroupCache.class);
        }

        install(new FactoryModuleBuilder()
                .implement(GroupHeart.class, SQLGroupHeart.class)
                .build(new TypeLiteral<ExtensionFactory<SQLGroupHeart, Group>>() {}));


        bind(GroupFactory.class).to(SQLGroupFactory.class);

        bind(MemberFactory.class).to(SQLMemberFactory.class);

        bind(GroupProvider.class).to(controller);

        Key<SQLRankPublisher> rankKey = get(SQLRankPublisher.class);

        // Group Publishers
        bind(GroupPublisher.class).to(SQLGroupPublisher.class);
        bind(GroupDestructor.class).to(SQLGroupDestructor.class);

        // Member Publishers
        bind(MemberPublisher.class).to(SQLMemberPublisher.class);

        // Rank publishers
        bind(RankPublisher.class).to(rankKey);

        bind(Locker.class).to(SQLLocker.class);


    }
}
