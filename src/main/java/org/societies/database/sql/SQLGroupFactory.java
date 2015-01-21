package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.joda.time.DateTime;
import org.societies.groups.ExtensionFactory;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.setting.SettingProvider;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a DefaultGroupFactory
 */
public class SQLGroupFactory implements GroupFactory {

    private final Provider<UUID> uuidProvider;
    private final ExtensionFactory<SQLGroupHeart, Group> heartFactory;

    private final SettingProvider settingProvider;
    private final ListeningExecutorService service;
    private final Queries queries;
    private final Set<ExtensionRoller> extensions;

    @Inject
    public SQLGroupFactory(
            Provider<UUID> uuidProvider,
            ExtensionFactory<SQLGroupHeart, Group> heartFactory,
            SettingProvider settingProvider,
            ListeningExecutorService service, Queries queries,
            @Named("member") Set<ExtensionRoller> extensions) {
        this.uuidProvider = uuidProvider;
        this.heartFactory = heartFactory;
        this.settingProvider = settingProvider;
        this.service = service;
        this.queries = queries;
        this.extensions = extensions;
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
        Group group = new Group(uuid);

        SQLGroupHeart heart = heartFactory.create(group);
        SQLSubject subject = new SQLSubject(uuid, settingProvider, queries, service, Queries.INSERT_SOCIETY_SETTING, Queries.SELECT_SOCIETY_SETTINGS);

        group.setGroupHeart(heart);
        group.setSubject(subject);

        for (ExtensionRoller extension : extensions) {
            extension.roll(group);
        }

        return group;
    }
}
