package net.catharos.societies.database.json;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.uuid.UUIDStorage;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.api.member.SocietyMember;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Represents a JSONMemberPublisher
 */
@SuppressWarnings("TypeParameterHidesVisibleType")
public final class JSONMemberPublisher implements
        MemberPublisher, MemberCreatedPublisher, MemberLastActivePublisher,
        MemberGroupPublisher, MemberRankPublisher, SettingPublisher, MemberDropPublisher {

    private final UUIDStorage uuidStorage;
    private final MemberMapper<?> mapper;
    private final ListeningExecutorService service;
    private final JSONProvider<SocietyMember> provider;

    @InjectLogger
    private Logger logger;

    @Inject
    public JSONMemberPublisher(@Named("member-root") File memberRoot, MemberMapper<SocietyMember> mapper, ListeningExecutorService service, JSONProvider<SocietyMember> provider) {
        this.provider = provider;
        this.uuidStorage = new UUIDStorage(memberRoot, "json");
        this.mapper = mapper;
        this.service = service;
    }

    private ListenableFuture<Member> publishMember(final Member member) {
        return service.submit(new Callable<Member>() {

            @Override
            public Member call() throws Exception {
                try {
                    provider.members.add((SocietyMember) member);//beautify cast?
                    mapper.writeMember(member, uuidStorage.getFile(member.getUUID()));
                } catch (Exception e) {
                    logger.catching(e);
                }

                return member;
            }
        });
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        publishMember(((Member) subject));
    }

    @Override
    public ListenableFuture<Member> publish(Member member) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> publishCreated(Member member, DateTime created) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> publishGroup(Member member, Group group) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> publishLastActive(Member member, DateTime date) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> publishRank(Member member, Rank rank) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> dropRank(Member member, Rank rank) {
        return publishMember(member);
    }

    @Override
    public ListenableFuture<Member> drop(final Member member) {
        return service.submit(new Callable<Member>() {

            @Override
            public Member call() throws Exception {
                provider.members.remove(member);

                File file = uuidStorage.getFile(member.getUUID());
                FileUtils.forceDelete(file);
                return member;
            }
        });
    }
}
