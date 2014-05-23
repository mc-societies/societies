package net.catharos.societies.member;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Member.class, SocietyMember.class)
                .build(MemberFactory.class));

        bind(MemberProvider.class).to(MemberCache.class);
    }
}
