package org.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import net.catharos.lib.shank.service.AbstractServiceModule;
import org.jooq.SQLDialect;
import org.societies.api.lock.Locker;
import org.societies.database.DSLProvider;
import org.societies.database.Database;
import org.societies.database.RemoteDatabase;
import org.societies.database.data.DataWorker;
import org.societies.database.data.queue.DefaultQueue;
import org.societies.database.data.queue.Queue;
import org.societies.group.OnlineGroupCache;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.cache.GroupCache;
import org.societies.groups.cache.MemberCache;
import org.societies.groups.cache.NaughtyGroupCache;
import org.societies.groups.group.*;
import org.societies.groups.member.*;
import org.societies.groups.rank.Rank;
import org.societies.groups.rank.RankFactory;
import org.societies.groups.rank.RankPublisher;
import org.societies.groups.rank.StaticRank;
import org.societies.groups.rank.memory.MemoryRank;
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

        bindNamedString(RemoteDatabase.DB_DATASOURCE_CLASS, "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");

        bind(SQLDialect.class).toInstance(SQLDialect.MYSQL);

        bind(DataWorker.class);

        bind(Queue.class).to(DefaultQueue.class);
        bindNamedInstance("auto-flush-interval", long.class, 5000L);
        bindNamedInstance("max-batch-idle", long.class, 5000L);
        bindNamedInstance("queue-time-unit", TimeUnit.class, TimeUnit.MILLISECONDS);
        bindNamedInstance("critical-batch-size", int.class, 100);

        bind(Database.class).to(RemoteDatabase.class);
        bind(DSLProvider.class).to(RemoteDatabase.class);


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


        install(new FactoryModuleBuilder()
                .implement(Rank.class, MemoryRank.class)
                .implement(Rank.class, Names.named("static"), StaticRank.class)
                .build(RankFactory.class));

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
