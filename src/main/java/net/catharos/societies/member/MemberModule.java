package net.catharos.societies.member;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.shank.AbstractModule;
import net.catharos.societies.database.sql.SQLModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    public static final Class<? extends SocietyMember> MEMBER_IMPLEMENTATION = BukkitSocietyMember.class;

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(SocietyMember.class, MEMBER_IMPLEMENTATION)
                .build(MemberFactory.class));

        bind(SocietyMember.class).to(MEMBER_IMPLEMENTATION);

        bind(SenderProvider.class).to(SenderAdapter.class);

        install(new SQLModule());
    }
}
