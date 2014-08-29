package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.SettingPublisher;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.SocietiesSettingsRecord;
import org.jooq.Insert;
import org.jooq.types.UShort;

/**
 * Represents a SettingPublisher
 */
class SQLSettingPublisher extends AbstractPublisher implements SettingPublisher {

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
                    Insert<SocietiesSettingsRecord> query = queries.getQuery(SQLQueries.INSERT_SOCIETY_SETTING);

                    query.bind(1, UUIDGen.toByteArray(subject.getUUID()));
                    query.bind(2, UUIDGen.toByteArray(target.getUUID()));
                    query.bind(3, UShort.valueOf(setting.getID()));
                    query.bind(4, setting.convert(value));

                    query.bind(5, UUIDGen.toByteArray(subject.getUUID()));
                    query.bind(6, UUIDGen.toByteArray(target.getUUID()));
                    query.bind(7, UShort.valueOf(setting.getID()));
                    query.bind(8, setting.convert(value));
                    query.execute();
                }
            });
        }
    }
}
