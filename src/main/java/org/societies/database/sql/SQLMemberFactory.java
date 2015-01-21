package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberFactory;
import org.societies.groups.request.DefaultParticipant;
import org.societies.groups.setting.SettingProvider;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a BukkitMemberFactory
 */
public class SQLMemberFactory implements MemberFactory {

    private final ExtensionFactory<? extends Sender, UUID> senderFactory;
    private final ExtensionFactory<SQLMemberHeart, Member> heartFactory;
    private final Set<ExtensionRoller> extensions;

    private final SettingProvider settingProvider;
    private final ListeningExecutorService service;
    private final Queries queries;


    @Inject
    public SQLMemberFactory(ExtensionFactory<Sender, UUID> senderFactory,
                            ExtensionFactory<SQLMemberHeart, Member> heartFactory,
                            Set<ExtensionRoller> extensions,
                            SettingProvider settingProvider, ListeningExecutorService service, Queries queries) {
        this.senderFactory = senderFactory;
        this.heartFactory = heartFactory;
        this.extensions = extensions;
        this.settingProvider = settingProvider;
        this.service = service;
        this.queries = queries;
    }

    @Override
    public Member create(UUID uuid) {
        Member member = new Member(uuid);

        Sender sender = senderFactory.create(uuid);
        DefaultParticipant participant = new DefaultParticipant(sender);
        SQLMemberHeart heart = heartFactory.create(member);

        SQLSubject subject = new SQLSubject(uuid, settingProvider, queries, service, Queries.INSERT_MEMBER_SETTING, Queries.SELECT_MEMBER_SETTINGS);

        member.setMemberHeart(heart);
        member.setSubject(subject);
        member.setParticipant(participant);
        member.setSender(sender);

        for (ExtensionRoller extension : extensions) {
            extension.roll(member);
        }

        return member;
    }
}
