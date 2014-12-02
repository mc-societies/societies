package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.joda.time.DateTime;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupComposite;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.group.GroupPublisher;
import org.societies.groups.setting.SettingProvider;

import java.util.UUID;

/**
 * Represents a DefaultGroupFactory
 */
public class SQLGroupFactory implements GroupFactory {

    private final Provider<UUID> uuidProvider;
    private final GroupPublisher groupPublisher;
    private final ExtensionFactory<SQLGroupHeart, Group> heartFactory;

    private final SettingProvider settingProvider;
    private final ListeningExecutorService service;
    private final SQLQueries queries;

    @Inject
    public SQLGroupFactory(
            Provider<UUID> uuidProvider,
            GroupPublisher groupPublisher,
            ExtensionFactory<SQLGroupHeart, Group> heartFactory, SettingProvider settingProvider, ListeningExecutorService service, SQLQueries queries) {
        this.uuidProvider = uuidProvider;
        this.groupPublisher = groupPublisher;
        this.heartFactory = heartFactory;
        this.settingProvider = settingProvider;
        this.service = service;
        this.queries = queries;
    }

    @Override
    public Group create(String name, String tag) {
        return create(uuidProvider.get(), name, tag);
    }

    @Override
    public Group create(String name, String tag, DateTime created) {
        return create(uuidProvider.get(), name, tag, created);
    }

    @Override
    public Group create(UUID uuid, String name, String tag) {
        return create(uuid, name, tag, DateTime.now());
    }

    @Override
    public Group create(final UUID uuid, String name, String tag, DateTime created) {
        GroupComposite group = new GroupComposite(uuid);

        SQLGroupHeart heart = heartFactory.create(group);
        SQLSubject subject = new SQLSubject(uuid, settingProvider, queries, service, SQLQueries.INSERT_SOCIETY_SETTING, SQLQueries.SELECT_SOCIETY_SETTINGS);

        group.setGroupHeart(heart);
        group.setSubject(subject);
        return group;
    }
}
