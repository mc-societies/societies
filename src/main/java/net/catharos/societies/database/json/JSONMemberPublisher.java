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
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Represents a JSONMemberPublisher
 */
public final class JSONMemberPublisher<M extends Member> implements
        MemberPublisher, MemberCreatedPublisher, MemberLastActivePublisher, MemberStatePublisher,
        MemberGroupPublisher, MemberRankPublisher, SettingPublisher {

    private final UUIDStorage uuidStorage;
    private final MemberMapper<?> mapper;
    private final ListeningExecutorService service;

    @Inject
    public JSONMemberPublisher(@Named("member-root") File memberRoot, MemberMapper<M> mapper, ListeningExecutorService service) {
        this.uuidStorage = new UUIDStorage(memberRoot, "json");
        this.mapper = mapper;
        this.service = service;
    }


    private <T extends Member> ListenableFuture<T> publishMember(final T member) {
        return service.submit(new Callable<T>() {

            @Override
            public T call() throws Exception {
                mapper.writeMember(member, uuidStorage.getFile(member.getUUID()));

                return member;
            }
        });
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        publishMember(((Member) subject));
    }

    @Override
    public <M extends Member> ListenableFuture<M> publish(M member) {
        return publishMember(member);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishCreated(M member, DateTime created) {
        return publishMember(member);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishGroup(M member, Group group) {
        return publishMember(member);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishLastActive(M member, DateTime date) {
        return publishMember(member);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishRank(M member, Rank rank) {
        return publishMember(member);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishState(M member, short state) {
        return publishMember(member);
    }
}
