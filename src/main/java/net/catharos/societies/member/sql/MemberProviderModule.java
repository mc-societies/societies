package net.catharos.societies.member.sql;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.groups.publisher.Publisher;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a MemberProviderModule
 */
public class MemberProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MemberQueries.class);

        Key<SQLMemberController> controller = Key.get(SQLMemberController.class);

        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {}).to(controller);
        bind(new TypeLiteral<MemberPublisher<SocietyMember>>() {}).to(controller);

        bindNamed("society-publisher", new TypeLiteral<Publisher<Member>>() {}).to(SocietyPublisher.class);
    }
}
