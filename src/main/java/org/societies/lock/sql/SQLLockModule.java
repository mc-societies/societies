package org.societies.lock.sql;

import com.google.inject.PrivateModule;
import org.societies.api.lock.Locker;

/**
 * Represents a SQLLockModule
 */
public class SQLLockModule extends PrivateModule {

    @Override
    protected void configure() {
        bind(LockQueries.class);
        bind(Locker.class).to(SQLLocker.class);

        expose(Locker.class);
    }
}
