package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.SettingPublisher;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.database.sql.layout.tables.records.SocietiesSettingsRecord;
import org.apache.logging.log4j.Logger;
import org.jooq.Insert;
import org.jooq.types.UShort;

/**
 * Represents a SettingPublisher
 */
class SQLSettingPublisher extends AbstractPublisher implements SettingPublisher {

    @InjectLogger
    private Logger logger;

    @Inject
    public SQLSettingPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public <V> void publish(final Subject subject, final Target target, final Setting<V> setting, final V value) {
        if (subject instanceof Group) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    byte[] converted;

                    try {
                        converted = setting.convert(subject, target, value);
                    } catch (SettingException e) {
                        logger.warn("Failed to convert setting %s! Subject: %s Target: %s Value: %s", setting, subject, target, value);
                        return;
                    }

                    Insert<SocietiesSettingsRecord> query = queries.getQuery(SQLQueries.INSERT_SOCIETY_SETTING);

                    byte[] subjectUUID = UUIDGen.toByteArray(subject.getUUID());
                    byte[] targetUUID = UUIDGen.toByteArray(target.getUUID());
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
    }
}
