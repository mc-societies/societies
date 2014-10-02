package net.catharos.societies.database.json;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.publisher.*;
import net.catharos.lib.shank.service.AbstractServiceModule;
import net.catharos.societies.member.SocietyMember;

import java.io.File;

/**
 * Represents a MemberProviderModule
 */
public class JSONModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        Key<JSONProvider<SocietyMember>> provider = Key.get(new TypeLiteral<JSONProvider<SocietyMember>>() {});

        bindService().to(provider);

        bind(new TypeLiteral<SocietyMapper<SocietyMember>>() {});

        // Member provider
        bind(new TypeLiteral<MemberProvider<SocietyMember>>() {}).to(provider);

        // Group provider
        bind(GroupProvider.class).to(provider);

        Key<JSONGroupPublisher<SocietyMember>> groupPublisher = Key.get(new TypeLiteral<JSONGroupPublisher<SocietyMember>>() {});

        // Group Publishers
        bind(GroupPublisher.class).to(groupPublisher);
        bind(GroupNamePublisher.class).to(groupPublisher);
        bind(GroupCreatedPublisher.class).to(groupPublisher);
        bind(SettingPublisher.class).to(groupPublisher);
        bind(GroupStatePublisher.class).to(groupPublisher);
        bind(GroupRankPublisher.class).to(groupPublisher);


        Key<JSONMemberPublisher<SocietyMember>> memberPublisher = Key.get(new TypeLiteral<JSONMemberPublisher<SocietyMember>>() {});

        // Member Publishers
        bind(new TypeLiteral<MemberPublisher<SocietyMember>>() {}).to(memberPublisher);
        bind(MemberGroupPublisher.class).to(memberPublisher);
        bind(MemberCreatedPublisher.class).to(memberPublisher);
        bind(MemberLastActivePublisher.class).to(memberPublisher);
        bind(MemberStatePublisher.class).to(memberPublisher);
        bind(MemberRankPublisher.class).to(memberPublisher);

        // Rank publishers
        bind(RankPublisher.class).to(groupPublisher);
        bind(RankDropPublisher.class).to(groupPublisher);

        bindNamedInstance("group-root", File.class, new File("/tmp/groups"));
        bindNamedInstance("member-root", File.class, new File("/tmp/members"));

    }


}
