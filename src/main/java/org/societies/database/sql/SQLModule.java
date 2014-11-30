package org.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberHeart;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.publisher.*;
import org.societies.member.OnlineMemberCache;

import java.util.concurrent.TimeUnit;

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


        bind(SQLQueries.class);

        Key<SQLProvider> controller = Key.get(SQLProvider.class);

        // Member provider
        bind(MemberCache.class).to(OnlineMemberCache.class);

        bind(new TypeLiteral<MemberProvider>() {})
                .to(controller);

        // Group provider
        if (cache) {
            bind(GroupCache.class).to(OnlineGroupCache.class);
        } else {
            bind(GroupCache.class).to(NaughtyGroupCache.class);
        }

        bind(GroupProvider.class).to(controller);

        Key<SQLRankPublisher> rankKey = Key.get(SQLRankPublisher.class);

        // Group Publishers
        bind(GroupPublisher.class).to(SQLGroupPublisher.class);
        bind(GroupNamePublisher.class).to(SQLNamePublisher.class);
        bind(GroupCreatedPublisher.class).to(SQLGroupCreatedPublisher.class);
        bind(SettingPublisher.class).to(SQLSettingPublisher.class);
        bind(GroupRankPublisher.class).to(rankKey);
        bind(GroupDropPublisher.class).to(SQLGroupDropPublisher.class);

        // Member Publishers
        bind(MemberPublisher.class)
                .to(SQLMemberPublisher.class);
        bind(MemberGroupPublisher.class).to(SQLMemberGroupPublisher.class);
        bind(MemberCreatedPublisher.class).to(SQLMemberCreatedPublisher.class);
        bind(MemberLastActivePublisher.class).to(SQLLastActivePublisher.class);
        bind(MemberRankPublisher.class).to(rankKey);

        // Rank publishers
        bind(RankPublisher.class).to(rankKey);
        bind(RankDropPublisher.class).to(rankKey);

        bind(Locker.class).to(SQLLocker.class);

        install(new FactoryModuleBuilder()
                .implement(MemberHeart.class, SQLMemberHearth.class)
                .build(new TypeLiteral<ExtensionFactory<MemberHeart, Member>>() {}));

    }
}
