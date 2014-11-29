package net.catharos.societies.database.json;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.publisher.*;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.api.lock.DummyLocker;
import net.catharos.societies.api.lock.Locker;

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

//        bind(new TypeLiteral<MemberProvider<Member>>() {}).to(CastSafe.<Key<? extends MemberProvider<Member>>>toGeneric(provider);

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
