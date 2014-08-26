package net.catharos.societies.database.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.*;
import net.catharos.groups.publisher.Publisher;
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

        bindNamed("society-publisher", new TypeLiteral<Publisher<Member>>() {}).to(SocietyPublisher.class);
        bindNamed("name-publisher", new TypeLiteral<Publisher<Group>>() {}).to(NamePublisher.class);
        bindNamed("lastactive-publisher", new TypeLiteral<Publisher<Group>>() {}).to(LastActivePublisher.class);
    }
}
