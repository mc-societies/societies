package net.catharos.societies.group;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.catharos.groups.DefaultGroup;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.Relation;
import net.catharos.groups.publisher.*;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.setting.Setting;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.Set;
import java.util.UUID;

/**
 * Represents a DefaultGroupFactory
 */
class DefaultGroupFactory implements GroupFactory {

    private final Provider<UUID> uuidProvider;
    private final GroupNamePublisher namePublisher;
    private final SettingPublisher settingPublisher;
    private final GroupRankPublisher groupRankPublisher;
    private final RankDropPublisher rankDropPublisher;
    private final GroupCreatedPublisher createdPublisher;
    private final Setting<Relation> relationSetting;
    private final Setting<Boolean> verifySetting;
    private final Set<Rank> defaultRanks;

    @Inject
    public DefaultGroupFactory(
            Provider<UUID> uuidProvider,
            GroupNamePublisher namePublisher,
            SettingPublisher settingPublisher,
            GroupRankPublisher groupRankPublisher,
            RankDropPublisher rankDropPublisher,
            GroupCreatedPublisher createdPublisher,
            Setting<Relation> relationSetting,
            @Named("verify") Setting<Boolean> verifySetting,
            @Named("default-ranks") Set<Rank> defaultRanks) {

        this.uuidProvider = uuidProvider;

        this.namePublisher = namePublisher;
        this.settingPublisher = settingPublisher;
        this.groupRankPublisher = groupRankPublisher;
        this.rankDropPublisher = rankDropPublisher;
        this.createdPublisher = createdPublisher;
        this.relationSetting = relationSetting;
        this.verifySetting = verifySetting;
        this.defaultRanks = defaultRanks;
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
        return create(uuid, name, tag, DateTime.now(), null);
    }

    @Override
    public Group create(UUID uuid, String name, String tag, DateTime created) {
        return create(uuid, name, tag, created, null);
    }

    @Override
    public Group create(UUID uuid, String name, String tag, DateTime created, @Nullable Group parent) {
        return new DefaultGroup(
                uuid, name, tag, created, parent,
                namePublisher,
                settingPublisher,
                groupRankPublisher,
                rankDropPublisher,
                createdPublisher,
                relationSetting,
                verifySetting,
                defaultRanks
        );
    }
}
