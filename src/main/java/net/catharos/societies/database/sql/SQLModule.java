package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.*;
import net.catharos.groups.cache.GroupCache;
import net.catharos.groups.cache.MemberCache;
import net.catharos.groups.cache.NaughtyGroupCache;
import net.catharos.groups.cache.NaughtyMemberCache;
import net.catharos.groups.publisher.*;
import net.catharos.lib.database.DSLProvider;
import net.catharos.lib.database.Database;
import net.catharos.lib.database.RemoteDatabase;
import net.catharos.lib.database.data.DataWorker;
import net.catharos.lib.database.data.queue.DefaultQueue;
import net.catharos.lib.database.data.queue.Queue;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.api.lock.Locker;
import net.catharos.societies.api.member.SocietyMember;
import net.catharos.societies.group.OnlineGroupCache;
import net.catharos.societies.member.OnlineMemberCache;
import org.jooq.SQLDialect;

import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberProviderModule
 */
public class SQLModule extends AbstractServiceModule {

    private final boolean cache;

    public SQLModule(boolean cache) {this.cache = cache;}

    @Override
    protected void configure() {
        bindService().to(TablesService.class);
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
        if (cache) {
            bind(new TypeLiteral<MemberCache<SocietyMember>>() {})
                    .to(new TypeLiteral<OnlineMemberCache<SocietyMember>>() {});
        } else {
            bind(new TypeLiteral<MemberCache<SocietyMember>>() {})
                    .to(new TypeLiteral<NaughtyMemberCache<SocietyMember>>() {});
        }

        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {})
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
    }
}
