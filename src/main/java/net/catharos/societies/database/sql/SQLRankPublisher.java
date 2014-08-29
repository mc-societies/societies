package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.GroupRankPublisher;
import net.catharos.groups.publisher.MemberRankPublisher;
import net.catharos.groups.publisher.RankDropPublisher;
import net.catharos.groups.publisher.RankPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.RanksRecord;
import org.jooq.Insert;
import org.jooq.Query;

/**
 * Represents a SQLGroupRankPublisher
 */
public class SQLRankPublisher extends AbstractPublisher implements RankPublisher, MemberRankPublisher, GroupRankPublisher, RankDropPublisher {

    @Inject
    public SQLRankPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Rank rank) {
        Insert<RanksRecord> query = queries.getQuery(SQLQueries.INSERT_RANK);
        query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
        query.bind(2, rank.getName());
        query.execute();
    }

    @Override
    public void publish(Group group, Rank rank) {
        Insert query = queries.getQuery(SQLQueries.INSERT_SOCIETY_RANK);
        query.bind(1, UUIDGen.toByteArray(group.getUUID()));
        query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
        query.execute();
    }

    @Override
    public void publish(Member member, Rank rank) {
        Insert query = queries.getQuery(SQLQueries.INSERT_MEMBER_RANK);
        query.bind(1, UUIDGen.toByteArray(member.getUUID()));
        query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
        query.execute();
    }

    @Override
    public void drop(Rank rank) {
        Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_RANK);
        query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
        query.execute();

        query = queries.getQuery(SQLQueries.DROP_MEMBER_RANK);
        query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
        query.execute();

        query = queries.getQuery(SQLQueries.DROP_RANK);
        query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
        query.execute();
    }
}
