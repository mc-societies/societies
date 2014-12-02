package org.societies.database.sql;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.lib.shank.service.AbstractService;
import net.catharos.lib.shank.service.lifecycle.LifecycleContext;
import org.apache.logging.log4j.Logger;
import org.societies.groups.rank.RankPublisher;
import org.societies.groups.rank.Rank;

import java.util.Set;

/**
 * Represents a RankService
 */
public class RankService extends AbstractService {

    private final Set<Rank> ranks;
    private final RankPublisher rankPublisher;

    @InjectLogger
    private Logger logger;

    @Inject
    public RankService(@Named("predefined-ranks") Set<Rank> ranks,
                       RankPublisher rankPublisher) {
        this.ranks = ranks;
        this.rankPublisher = rankPublisher;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        for (Rank rank : ranks) {
            rankPublisher.publish(rank);
            logger.info("Inserting rank {0} in database...", rank.getName());
        }
    }
}
