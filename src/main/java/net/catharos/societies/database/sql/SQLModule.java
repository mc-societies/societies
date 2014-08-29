package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.GroupPublisher;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.groups.publisher.LastActivePublisher;
import net.catharos.groups.publisher.MemberGroupPublisher;
import net.catharos.groups.publisher.NamePublisher;
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


        bindNamed("group-publisher", new TypeLiteral<MemberPublisher<SocietyMember>>() {}).to(controller);

        bindNamed("forward", new TypeLiteral<MemberProvider<SocietyMember>>() {}).to(controller);
        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {})
                .to(new TypeLiteral<OnlineCacheMemberProvider<SocietyMember>>() {});

        bind(GroupProvider.class).to(controller);
        bind(GroupPublisher.class).to(controller);

        bind(MemberGroupPublisher.class).to(SQLMemberGroupPublisher.class);
        bind(NamePublisher.class).to(SQLNamePublisher.class);
        bind(LastActivePublisher.class).to(SQLLastActivePublisher.class);
    }
}
