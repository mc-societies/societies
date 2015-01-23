package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import org.societies.database.QueryProvider;

/**
 * Represents a AbstractPublisher
 */
public abstract class AbstractPublisher {

    protected final ListeningExecutorService service;
    protected final QueryProvider queries;

    public AbstractPublisher(ListeningExecutorService service, QueryProvider queries) {
        this.service = service;
        this.queries = queries;
    }
}
