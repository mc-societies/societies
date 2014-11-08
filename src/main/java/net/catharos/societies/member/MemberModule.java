package net.catharos.societies.member;

import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SenderProvider.class).to(SenderAdapter.class);
    }
}
