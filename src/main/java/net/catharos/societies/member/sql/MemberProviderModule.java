package net.catharos.societies.member.sql;

import com.google.inject.TypeLiteral;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a MemberProviderModule
 */
public class MemberProviderModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MemberQueries.class);

        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {}).to(SQLMemberProvider.class);
    }
}
