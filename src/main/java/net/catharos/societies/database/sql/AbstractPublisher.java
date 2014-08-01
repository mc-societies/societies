package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import net.catharos.groups.publisher.Publisher;

/**
 * Represents a AbstractPublisher
 */
abstract class AbstractPublisher<T> implements Publisher<T> {
    protected final ListeningExecutorService service;
    protected final SQLQueries queries;

    public AbstractPublisher(ListeningExecutorService service, SQLQueries queries) {
        this.service = service;
        this.queries = queries;
    }
}
