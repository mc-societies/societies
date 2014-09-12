package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.*;
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

        Key<SQLController> controller = Key.get(SQLController.class);

        // Member publisher
        bind(new TypeLiteral<MemberPublisher<SocietyMember>>() {}).to(controller);

        // Member provider
        bind(new TypeLiteral<MemberCache<SocietyMember>>() {})
                .to(new TypeLiteral<OnlineMemberCache<SocietyMember>>() {});
        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {})
                .to(controller);

        // Group provider
        bind(GroupCache.class).to(OnlineGroupCache.class);
        bind(GroupProvider.class).to(controller);

        // Group publisher
        bind(GroupPublisher.class).to(controller);

        //Publishers
        bind(MemberGroupPublisher.class).to(SQLMemberGroupPublisher.class);
        bind(NamePublisher.class).to(SQLNamePublisher.class);
        bind(SettingPublisher.class).to(SQLSettingPublisher.class);
        bind(LastActivePublisher.class).to(SQLLastActivePublisher.class);

        // State publishers
        Key<SQLStatePublisher> statePublisherKey = Key.get(SQLStatePublisher.class);
        bind(GroupStatePublisher.class).to(statePublisherKey);
        bind(MemberStatePublisher.class).to(statePublisherKey);

        // Rank publishers
        Key<SQLRankPublisher> rankKey = Key.get(SQLRankPublisher.class);
        bind(RankPublisher.class).to(rankKey);
        bind(MemberRankPublisher.class).to(rankKey);
        bind(RankDropPublisher.class).to(rankKey);
        bind(GroupRankPublisher.class).to(rankKey);
    }
}
