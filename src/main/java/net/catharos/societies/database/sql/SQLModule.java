package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.GroupCache;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.MemberCache;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.publisher.*;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.group.OnlineGroupCache;
import net.catharos.societies.member.OnlineMemberCache;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a MemberProviderModule
 */
public class SQLModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(CleanupService.class);

        bind(SQLQueries.class);

        Key<SQLProvider> controller = Key.get(SQLProvider.class);

        // Member provider
        bind(new TypeLiteral<MemberCache<SocietyMember>>() {})
                .to(new TypeLiteral<OnlineMemberCache<SocietyMember>>() {});
        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {})
                .to(controller);

        // Group provider
        bind(GroupCache.class).to(OnlineGroupCache.class);
        bind(GroupProvider.class).to(controller);

        Key<SQLStatePublisher> statePublisherKey = Key.get(SQLStatePublisher.class);
        Key<SQLRankPublisher> rankKey = Key.get(SQLRankPublisher.class);

        // Group Publishers
        bind(GroupPublisher.class).to(SQLGroupPublisher.class);
        bind(GroupNamePublisher.class).to(SQLNamePublisher.class);
        bind(GroupCreatedPublisher.class).to(SQLGroupCreatedPublisher.class);
        bind(SettingPublisher.class).to(SQLSettingPublisher.class);
        bind(GroupStatePublisher.class).to(statePublisherKey);
        bind(GroupRankPublisher.class).to(rankKey);

        // Member Publishers
        bind(new TypeLiteral<MemberPublisher<SocietyMember>>() {})
                .to(new TypeLiteral<SQLMemberPublisher<SocietyMember>>() {});
        bind(MemberGroupPublisher.class).to(SQLMemberGroupPublisher.class);
        bind(MemberCreatedPublisher.class).to(SQLMemberCreatedPublisher.class);
        bind(MemberLastActivePublisher.class).to(SQLLastActivePublisher.class);
        bind(MemberStatePublisher.class).to(statePublisherKey);
        bind(MemberRankPublisher.class).to(rankKey);

        // Rank publishers
        bind(RankPublisher.class).to(rankKey);
        bind(RankDropPublisher.class).to(rankKey);

    }
}
