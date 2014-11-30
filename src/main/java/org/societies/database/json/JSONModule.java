package org.societies.database.json;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.lib.shank.service.AbstractServiceModule;
import org.societies.api.lock.DummyLocker;
import org.societies.api.lock.Locker;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.DefaultMemberHeart;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberHeart;
import org.societies.groups.member.MemberProvider;
import org.societies.groups.publisher.*;

/**
 * Represents a MemberProviderModule
 */
public class JSONModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        Key<JSONProvider> provider = Key.get(new TypeLiteral<JSONProvider>() {});

        bindService().to(provider);

        bind(MemberMapper.class);

        // Member provider
        bind(MemberProvider.class).to(provider);

        install(new FactoryModuleBuilder()
                .implement(MemberHeart.class, DefaultMemberHeart.class)
                .build(new TypeLiteral<ExtensionFactory<MemberHeart, Member>>() {}));

        // Group provider
        bind(GroupProvider.class).to(provider);

        Key<JSONGroupPublisher> groupPublisher = Key
                .get(new TypeLiteral<JSONGroupPublisher>() {});

        // Group Publishers
        bind(GroupPublisher.class).to(groupPublisher);
        bind(GroupNamePublisher.class).to(groupPublisher);
        bind(GroupCreatedPublisher.class).to(groupPublisher);
        bind(SettingPublisher.class).to(groupPublisher);
        bind(GroupRankPublisher.class).to(groupPublisher);
        bind(GroupDropPublisher.class).to(groupPublisher);

        Key<JSONMemberPublisher> memberPublisher = Key
                .get(new TypeLiteral<JSONMemberPublisher>() {});

        // Member Publishers
        bind(MemberPublisher.class).to(provider);
        bind(MemberGroupPublisher.class).to(memberPublisher);
        bind(MemberCreatedPublisher.class).to(memberPublisher);
        bind(MemberLastActivePublisher.class).to(memberPublisher);
        bind(MemberRankPublisher.class).to(memberPublisher);
        bind(MemberDropPublisher.class).to(provider);

        // Rank publishers
        bind(RankPublisher.class).to(groupPublisher);
        bind(RankDropPublisher.class).to(groupPublisher);


        bind(Locker.class).to(DummyLocker.class);
    }


}
