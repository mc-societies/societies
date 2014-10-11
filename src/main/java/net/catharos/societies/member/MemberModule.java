package net.catharos.societies.member;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.MemberFactory;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a MemberModule
 */
public class MemberModule extends AbstractModule {

    public static final Class<? extends SocietyMember> MEMBER_IMPLEMENTATION = BukkitSocietyMember.class;

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(SocietyMember.class, MEMBER_IMPLEMENTATION)
                .build(new TypeLiteral<MemberFactory<SocietyMember>>() {}));

        bind(SocietyMember.class).to(MEMBER_IMPLEMENTATION);

        bind(SenderProvider.class).to(SenderAdapter.class);
    }
}
