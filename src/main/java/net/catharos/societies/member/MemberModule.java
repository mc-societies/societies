package net.catharos.societies.member;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.MemberCache;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .build(MemberFactory.class));

        bind(MemberCache.class).to(LoadingMemberCache.class);
    }
}
