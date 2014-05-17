package net.catharos.societies.member;

import net.catharos.groups.MemberCache;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MemberCache.class).to(LoadingMemberCache.class);
    }
}
