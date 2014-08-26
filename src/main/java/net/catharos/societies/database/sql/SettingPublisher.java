package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import net.catharos.groups.setting.subject.Subject;

/**
 * Represents a SettingPublisher
 */
public class SettingPublisher extends AbstractPublisher<Subject> {

    public SettingPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Subject> update(Subject update) {
        return null;
    }
}
