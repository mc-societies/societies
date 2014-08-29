package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupPublisher;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.groups.publisher.*;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a MemberProviderModule
 */
public class SQLModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SQLQueries.class);

        Key<SQLController> controller = Key.get(SQLController.class);


        bind(new TypeLiteral<MemberPublisher<SocietyMember>>() {}).to(controller);

        bindNamed("forward", new TypeLiteral<MemberProvider<SocietyMember>>() {}).to(controller);
        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {})
                .to(new TypeLiteral<OnlineCacheMemberProvider<SocietyMember>>() {});

        bind(GroupProvider.class).to(controller);
        bind(GroupPublisher.class).to(controller);
        bind(GroupPublisher.class).to(controller);

        bind(MemberGroupPublisher.class).to(SQLMemberGroupPublisher.class);
        bind(NamePublisher.class).to(SQLNamePublisher.class);
        bind(LastActivePublisher.class).to(SQLLastActivePublisher.class);
        bind(SettingPublisher.class).to(SQLSettingPublisher.class);

        bind(GroupStatePublisher.class).to(SQLGroupStatePublisher.class);
        bind(MemberStatePublisher.class).to(SQLMemberStatePublisher.class);

        Key<SQLRankPublisher> rankKey = Key.get(SQLRankPublisher.class);
        bind(RankPublisher.class).to(rankKey);
        bind(MemberRankPublisher.class).to(rankKey);
        bind(RankDropPublisher.class).to(rankKey);
        bind(GroupRankPublisher.class).to(rankKey);
    }
}
