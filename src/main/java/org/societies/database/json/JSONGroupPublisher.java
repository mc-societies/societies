package org.societies.database.json;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.cqengine.query.Query;
import net.catharos.lib.core.uuid.UUIDStorage;
import net.catharos.lib.shank.logging.InjectLogger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupHeart;
import org.societies.groups.publisher.*;
import org.societies.groups.rank.Rank;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.Target;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.or;

/**
 * Represents a JSONMemberPublisher
 */
final class JSONGroupPublisher implements
        GroupPublisher,
        GroupNamePublisher, GroupCreatedPublisher,
        GroupRankPublisher,
        RankPublisher, RankDropPublisher,
        SettingPublisher,
        GroupDropPublisher {

    private final UUIDStorage uuidStorage;
    private final GroupMapper mapper;
    private final ListeningExecutorService service;
    private final JSONProvider provider;

    @InjectLogger
    private Logger logger;


    @Inject
    public JSONGroupPublisher(@Named("group-root") File groupRoot, GroupMapper mapper, ListeningExecutorService service, JSONProvider provider) {
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

    public ListenableFuture<GroupHeart> publish(final GroupHeart group) {
        return Futures.transform(publish((Group) group), new Function<Group, GroupHeart>() {
            @javax.annotation.Nullable
            @Override
            public GroupHeart apply(@Nullable Group input) {
                return input;
            }
        });
    }


    private Group publish0(final Group group) throws IOException {
        try {
            provider.groups.add(group);
            mapper.writeGroup(group, uuidStorage.getFile(group.getUUID()));
        } catch (Exception e) {
            logger.catching(e);
        }
        return group;
    }

    public ListenableFuture<GroupHeart> defaultPublish(final GroupHeart group) {
        return service.submit(new Callable<GroupHeart>() {

            @Override
            public GroupHeart call() throws Exception {
                publish0(group.getHolder());
                return group;
            }
        });
    }

    @Override
    public ListenableFuture<GroupHeart> publishCreated(GroupHeart group, DateTime created) {
        return defaultPublish(group.getHolder());
    }

    @Override
    public ListenableFuture<GroupHeart> publishName(GroupHeart group, String name) {
        return publish(group);
    }

    @Override
    public ListenableFuture<GroupHeart> publishTag(GroupHeart group, String tag) {
        return publish(group);
    }

    @Override
    public ListenableFuture<Rank> publish(final Rank rank) {
        return Futures.transform(defaultPublish(rank.getGroup()), new Function<GroupHeart, Rank>() {
            @javax.annotation.Nullable
            @Override
            public Rank apply(@Nullable GroupHeart input) {
                return rank;
            }
        });
    }

    @Override
    public ListenableFuture<Rank> drop(final Rank rank) {
        return Futures.transform(defaultPublish(rank.getGroup()), new Function<GroupHeart, Rank>() {
            @javax.annotation.Nullable
            @Override
            public Rank apply(@Nullable GroupHeart input) {
                return rank;
            }
        });
    }

    @Override
    public ListenableFuture<GroupHeart> publishRank(GroupHeart group, Rank rank) {
        return defaultPublish(group);
    }

    @Override
    public <V> void publish(Subject subject, Target target, Setting<V> setting, @Nullable V value) {
        defaultPublish(((Group) subject));
    }

    @Override
    public ListenableFuture<Group> drop(final Group group) {
        return service.submit(new Callable<Group>() {

            @Override
            public Group call() throws Exception {
                provider.groups.remove(group);

                File file = uuidStorage.getFile(group.getUUID());
                FileUtils.forceDelete(file);
                return group;
            }
        });
    }
}
