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
public final class JSONGroupPublisher<M extends Member> implements
        GroupPublisher,
        GroupNamePublisher, GroupCreatedPublisher, GroupStatePublisher,
        GroupRankPublisher,
        RankPublisher, RankDropPublisher,
        SettingPublisher {

    private final UUIDStorage uuidStorage;
    private final SocietyMapper<?> mapper;
    private final ListeningExecutorService service;
    private final JSONProvider<M> provider;

    @Inject
    public JSONGroupPublisher(@Named("group-root") File groupRoot, SocietyMapper<M> mapper, ListeningExecutorService service, JSONProvider<M> provider) {
        this.provider = provider;
        this.uuidStorage = new UUIDStorage(groupRoot, "json");
        this.mapper = mapper;
        this.service = service;
    }

    @Override
    public ListenableFuture<Group> publish(final Group group) {
        return service.submit(new Callable<Group>() {

            @Override
            public Group call() throws Exception {
                mapper.writeGroup(group, uuidStorage.getFile(group.getUUID()));
                provider.groups.add(group);
                return group;
            }
        });
    }

    @Override
    public void publish(Group group, DateTime created) {
        publish(group);
    }

    @Override
    public void publish(Group group, String name) {
        publish(group);
    }


    @Override
    public void publishRank(Group group, Rank rank) {
        publish(group);
    }

    @Override
    public void publish(Group group, short state) {
        publish(group);
    }

    @Override
    public void drop(Group group, Rank rank) {
        publish(group);
    }

    @Override
    public void publish(Group group, Rank rank) {
        publish(group);
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        publish(((Group) subject));
    }
}
