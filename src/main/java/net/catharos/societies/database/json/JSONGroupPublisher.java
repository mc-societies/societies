package net.catharos.societies.database.json;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.cqengine.query.Query;
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
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.or;

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
    private final GroupMapper mapper;
    private final ListeningExecutorService service;
    private final JSONProvider<M> provider;

    @Inject
    public JSONGroupPublisher(@Named("group-root") File groupRoot, GroupMapper mapper, ListeningExecutorService service, JSONProvider<M> provider) {
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
                String tag = group.getTag();
                String name = group.getName();

                Query<Group> query = or(
                        contains(JSONProvider.GROUP_CLEAN_TAG, tag),
                        contains(JSONProvider.GROUP_TAG, name)
                );

                if (provider.groups.retrieve(query).isNotEmpty()) {
                    return null;
                }

                return publish0(group);
            }
        });
    }


    private Group publish0(final Group group) throws IOException {
        provider.groups.add(group);
        mapper.writeGroup(group, uuidStorage.getFile(group.getUUID()));
        return group;
    }

    public ListenableFuture<Group> defaultPublish(final Group group) {
        return service.submit(new Callable<Group>() {

            @Override
            public Group call() throws Exception {
                return publish0(group);
            }
        });
    }

    @Override
    public void publish(Group group, DateTime created) {
        defaultPublish(group);
    }

    @Override
    public void publish(Group group, String name) {
        publish(group);
    }

    @Override
    public void publishRank(Group group, Rank rank) {
        defaultPublish(group);
    }

    @Override
    public void publish(Group group, short state) {
        defaultPublish(group);
    }

    @Override
    public void drop(Group group, Rank rank) {
        defaultPublish(group);
    }

    @Override
    public void publish(Group group, Rank rank) {
        defaultPublish(group);
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        defaultPublish(((Group) subject));
    }
}
