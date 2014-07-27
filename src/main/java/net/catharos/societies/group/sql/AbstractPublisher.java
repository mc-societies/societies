package net.catharos.societies.group.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import net.catharos.groups.Group;
import net.catharos.groups.publisher.Publisher;

/**
 * Represents a AbstractPublisher
 */
abstract class AbstractPublisher implements Publisher<Group> {
    protected final ListeningExecutorService service;
    protected final SocietyQueries queries;

    public AbstractPublisher(ListeningExecutorService service, SocietyQueries queries) {
        this.service = service;
        this.queries = queries;
    }
}
