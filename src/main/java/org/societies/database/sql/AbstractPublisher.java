package org.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Represents a AbstractPublisher
 */
abstract class AbstractPublisher {

    protected final ListeningExecutorService service;
    protected final Queries queries;

    public AbstractPublisher(ListeningExecutorService service, Queries queries) {
        this.service = service;
        this.queries = queries;
    }
}
