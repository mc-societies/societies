package org.societies.database.json;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.cqengine.query.Query;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.shank.logging.InjectLogger;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupDestructor;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupPublisher;

import javax.inject.Provider;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;

import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.or;

/**
 * Represents a JSONMemberPublisher
 */
final class JSONGroupPublisher implements GroupPublisher, GroupDestructor {

    private final Provider<UUID> uuid;
    private final UUIDStorage uuidStorage;
    private final GroupMapper mapper;
    private final ListeningExecutorService service;
    private final JSONProvider provider;
    private final GroupFactory groupFactory;

    @InjectLogger
    private Logger logger;

    @Inject
    public JSONGroupPublisher(@Named("group-root") File groupRoot, Provider<UUID> uuid, GroupMapper mapper, ListeningExecutorService service, JSONProvider provider, GroupFactory groupFactory) {
        this.uuid = uuid;
        this.provider = provider;
        this.groupFactory = groupFactory;
        this.uuidStorage = new UUIDStorage(groupRoot, "json");
        this.mapper = mapper;
        this.service = service;
    }

    @Override
    public ListenableFuture<Group> publish(final UUID uuid, final String name, final String tag, final DateTime created) {
        return service.submit(new Callable<Group>() {

            @Override
            public Group call() throws Exception {

                Query<Group> query = or(
                        contains(JSONProvider.GROUP_CLEAN_TAG, tag),
                        contains(JSONProvider.GROUP_TAG, name)
                );

                if (provider.groups.retrieve(query).isNotEmpty()) {
                    return null;
                }

                Group group = groupFactory.create(uuid, name, tag, created);

                publish0(group);
                return group;
            }
        });
    }

    private void publish0(Group group) {
        try {
            provider.groups.add(group);
            mapper.writeGroup(group, uuidStorage.getFile(group.getUUID()));
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public ListenableFuture<Group> publish(String name, String tag) {
        return publish(uuid.get(), name, tag, DateTime.now());
    }

    @Override
    public ListenableFuture<Group> publish(final Group group) {
        return service.submit(new Callable<Group>() {

            @Override
            public Group call() throws Exception {
                publish0(group);
                return group;
            }
        });
    }

    @Override
    public ListenableFuture<Group> destruct(final Group group) {
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
