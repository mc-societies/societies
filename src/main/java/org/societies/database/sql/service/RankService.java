package org.societies.database.sql.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.uuid.UUIDGen;
import org.apache.logging.log4j.Logger;
import org.jooq.Insert;
import org.shank.logging.InjectLogger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.database.sql.Queries;
import org.societies.database.sql.layout.tables.records.RanksRecord;
import org.societies.groups.rank.Rank;

import java.util.Set;

/**
 * Represents a RankService
 */
public class RankService extends AbstractService {

    private final Set<Rank> ranks;
    private final Queries queries;

    @InjectLogger
    private Logger logger;

    @Inject
    public RankService(@Named("predefined-ranks") Set<Rank> ranks, Queries queries) {
        this.ranks = ranks;
        this.queries = queries;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        for (Rank rank : ranks) {
            byte[] uuid = UUIDGen.toByteArray(rank.getUUID());
            String name = rank.getName();
            int priority = rank.getPriority();

            Insert<RanksRecord> query = queries.getQuery(Queries.INSERT_RANK);

            query.bind(1, uuid);
            query.bind(2, name);
            query.bind(3, priority);
            query.bind(4, uuid);
            query.bind(5, name);
            query.bind(6, priority);
            query.execute();
            logger.info("Inserting rank {0} in database...", rank.getName());
        }
    }
}
