package net.catharos.societies.member;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.Member;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.member.sql.MemberProviderModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(Member.class, SocietyMember.class)
                .build(MemberFactory.class));

        bind(SenderProvider.class).to(SenderAdapter.class);

        install(new MemberProviderModule());
    }
}
