package org.societies.database.json;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.googlecode.cqengine.query.Query;
import net.catharos.lib.core.uuid.UUIDStorage;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupPublisher;

import javax.inject.Provider;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.or;

/**
 * Represents a JSONMemberPublisher
 */
final class JSONGroupPublisher implements GroupPublisher {

    private final Provider<UUID> uuid;
    private final UUIDStorage uuidStorage;
    private final GroupMapper mapper;
    private final JSONProvider provider;
    private final GroupFactory groupFactory;

    private final Logger logger;

    @Inject
    public JSONGroupPublisher(@Named("group-root") File groupRoot,
                              Provider<UUID> uuid, GroupMapper mapper,
                              JSONProvider provider,
                              GroupFactory groupFactory, Logger logger) {
        this.uuid = uuid;
        this.provider = provider;
        this.groupFactory = groupFactory;
        this.logger = logger;
        this.uuidStorage = new UUIDStorage(groupRoot, "json");
        this.mapper = mapper;
    }

    @Override
    public Group publish(final UUID uuid, final String name, final String tag, final DateTime created) {

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

    private void publish0(Group group) {
        try {
            provider.groups.add(group);
            File file = uuidStorage.getFile(group.getUUID());

            FileOutputStream stream = new FileOutputStream(file);
            FileChannel channel = stream.getChannel();
            channel.lock();

            mapper.createNode(group, new BufferedOutputStream(stream));
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    @Override
    public Group publish(String name, String tag) {
        return publish(uuid.get(), name, tag, DateTime.now());
    }

    @Override
    public Group publish(final Group group) {
        publish0(group);
        return group;
    }

    @Override
    public Group destruct(final Group group) {
        provider.groups.remove(group);

        try {
            uuidStorage.delete(group.getUUID());
        } catch (IOException e) {
            e.printStackTrace();  //fixme
            return null;
        }

        return group;
    }
}
