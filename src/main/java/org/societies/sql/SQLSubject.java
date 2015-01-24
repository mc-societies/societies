package org.societies.sql;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.apache.logging.log4j.Logger;
import org.jooq.Insert;
import org.jooq.Record3;
import org.jooq.Select;
import org.jooq.types.UShort;
import org.shank.logging.InjectLogger;
import org.societies.database.QueryKey;
import org.societies.database.QueryProvider;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.SettingProvider;
import org.societies.groups.setting.subject.AbstractSubject;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.SimpleTarget;
import org.societies.groups.setting.target.Target;

import java.util.UUID;

/**
 * Represents a SQLSubject
 */
class SQLSubject extends AbstractSubject {

    private final QueryProvider queries;
    private final UUID uuid;
    private final SettingProvider settingProvider;
    private final ListeningExecutorService service;
    private final QueryKey<? extends Insert> insert;
    private final QueryKey<Select<Record3<UUID, UShort, byte[]>>> select;

    @InjectLogger
    private Logger logger;

    protected SQLSubject(UUID uuid,
                         SettingProvider settingProvider,
                         QueryProvider queries, ListeningExecutorService service,
                         QueryKey<? extends Insert> insert,
                         QueryKey<Select<Record3<UUID, UShort, byte[]>>> select) {
        this.queries = queries;
        this.uuid = uuid;
        this.settingProvider = settingProvider;
        this.service = service;
        this.insert = insert;
        this.select = select;
    }

    @Override
    public <V> void set(final Setting<V> setting, final Target target, final V value) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                Subject subject = SQLSubject.this;

                byte[] converted;

                try {
                    converted = setting.convert(subject, target, value);
                } catch (SettingException ignored) {
                    logger.warn("Failed to convert setting %s! Subject: %s Target: %s Value: %s", setting, subject, target, value);
                    return;
                }

                Insert query = queries.getQuery(insert);

                UUID subjectUUID = subject.getUUID();
                UUID targetUUID = target.getUUID();
                UShort settingID = UShort.valueOf(setting.getID());

                query.bind(1, subjectUUID);
                query.bind(2, targetUUID);
                query.bind(3, settingID);
                query.bind(4, converted);

                query.bind(5, subjectUUID);
                query.bind(6, targetUUID);
                query.bind(7, settingID);
                query.bind(8, converted);
                query.execute();
            }
        });
    }

    @Override
    public <V> void remove(Setting<V> setting, Target target) {
        set(setting, target, null);
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public <V> V get(Setting<V> setting, Target target) {
        Table<Setting, Target, Object> table = getSettings();
        return (V) table.get(setting, target);//optimize
    }

    @Override
    public Table<Setting, Target, Object> getSettings() {
        Table<Setting, Target, Object> table = HashBasedTable.create();

        Select<Record3<UUID, UShort, byte[]>> query = queries.getQuery(select);
        query.bind(1, getUUID());

        for (Record3<UUID, UShort, byte[]> settingRecord : query.fetch()) {
            int settingID = settingRecord.value2().intValue();

            Setting setting = settingProvider.getSetting(settingID);

            if (setting == null) {
//                logger.warn("Failed to convert setting %s!", settingID);
                continue;
            }

            UUID targetUUID = settingRecord.value1();
            Target target;

            if (targetUUID == null) {
                target = this;
            } else {
                target = new SimpleTarget(targetUUID);
            }

            Object value;
            try {
                value = setting.convert(this, target, settingRecord.value3());
            } catch (SettingException ignored) {
                continue;
            }

            table.put(setting, target, value);
        }

        return table;
    }
}