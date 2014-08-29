package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.publisher.SettingPublisher;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;

/**
 * Represents a SettingPublisher
 */
class SQLSettingPublisher extends AbstractPublisher implements SettingPublisher {

    @Inject
    public SQLSettingPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public <V> void publish(Subject group, Target target, Setting<V> setting, V value) {

    }
}
