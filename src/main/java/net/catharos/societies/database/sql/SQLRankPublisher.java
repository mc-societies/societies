package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
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
import net.catharos.societies.database.sql.layout.tables.records.RanksRecord;
import org.jooq.Insert;
import org.jooq.Query;

import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupRankPublisher
 */
class SQLRankPublisher extends AbstractPublisher implements RankPublisher, MemberRankPublisher, GroupRankPublisher, RankDropPublisher {

    @Inject
    public SQLRankPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Rank> publish(final Rank rank) {
        return service.submit(new Callable<Rank>() {
            @Override
            public Rank call() throws Exception {
                Insert<RanksRecord> query = queries.getQuery(SQLQueries.INSERT_RANK);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.bind(2, rank.getName());
                query.execute();
                return rank;
            }
        });
    }

    @Override
    public ListenableFuture<Group> publishRank(final Group group, final Rank rank) {
        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Insert query = queries.getQuery(SQLQueries.INSERT_SOCIETY_RANK);
                query.bind(1, UUIDGen.toByteArray(group.getUUID()));
                query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return group;
            }
        });
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishRank(final M member, final Rank rank) {
        return service.submit(new Callable<M>() {
            @Override
            public M call() throws Exception {
                Insert query = queries.getQuery(SQLQueries.INSERT_MEMBER_RANK);
                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
                query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return member;
            }
        });
    }

    @Override
    public ListenableFuture<Rank> drop(final Rank rank) {
        return service.submit(new Callable<Rank>() {
            @Override
            public Rank call() throws Exception {
                Query query = queries.getQuery(SQLQueries.DROP_SOCIETY_RANK);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();

                query = queries.getQuery(SQLQueries.DROP_MEMBER_RANK);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();

                query = queries.getQuery(SQLQueries.DROP_RANK);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return rank;
            }
        });
    }
}
