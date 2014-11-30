package org.societies.database.sql;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Record3;
import org.jooq.Select;
import org.jooq.types.UShort;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.SettingPublisher;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.subject.AbstractPublishingSubject;
import org.societies.groups.setting.target.SimpleTarget;
import org.societies.groups.setting.target.Target;

import java.util.UUID;

/**
 * Represents a SQLSubject
 */
public class SQLSubject extends AbstractPublishingSubject {

    private final SQLQueries queries;
    private final Member member;
    private final SettingProvider settingProvider;

    protected SQLSubject(SettingPublisher settingPublisher, SQLQueries queries, Member member, SettingProvider settingProvider) {
        super(settingPublisher);
        this.queries = queries;
        this.member = member;
        this.settingProvider = settingProvider;
    }

    @Override
    public UUID getUUID() {
        return member.getUUID();
    }

    @Override
    public <V> V get(Setting<V> setting, Target target) {
        return (V) getSettings().get(setting, target); //todo
    }

    @Override
    public Table<Setting, Target, Object> getSettings() {
        Table<Setting, Target, Object> table = HashBasedTable.create();

        Select<Record3<byte[], UShort, byte[]>> query = queries.getQuery(SQLQueries.SELECT_MEMBER_SETTINGS);
        query.bind(1, UUIDGen.toByteArray(getUUID()));

        for (Record3<byte[], UShort, byte[]> settingRecord : query.fetch()) {
            int settingID = settingRecord.value2().intValue();

            Setting setting = settingProvider.getSetting(settingID);

            if (setting == null) {
//                logger.warn("Failed to convert setting %s!", settingID);
                continue;
            }

            byte[] targetUUID = settingRecord.value1();
            Target target;

            if (targetUUID == null) {
                target = this;
            } else {
                target = new SimpleTarget(UUIDGen.toUUID(targetUUID));
            }

            Object value;
            try {
                value = setting.convert(this, target, settingRecord.value3());
            } catch (SettingException e) {
                continue;
            }

            table.put(setting, target, value);
        }

        return table;
    }
}
