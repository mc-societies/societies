package org.societies.database.sql;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Insert;
import org.jooq.Query;
import org.societies.database.sql.layout.tables.records.RanksRecord;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.GroupRankPublisher;
import org.societies.groups.publisher.MemberRankPublisher;
import org.societies.groups.publisher.RankDropPublisher;
import org.societies.groups.publisher.RankPublisher;
import org.societies.groups.rank.Rank;

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
                byte[] uuid = UUIDGen.toByteArray(rank.getUUID());
                String name = rank.getName();
                int priority = rank.getPriority();

                Insert<RanksRecord> query = queries.getQuery(SQLQueries.INSERT_RANK);

                query.bind(1, uuid);
                query.bind(2, name);
                query.bind(3, priority);
                query.bind(4, uuid);
                query.bind(5, name);
                query.bind(6, priority);
                query.execute();
                return rank;
            }
        });
    }

    @Override
    public ListenableFuture<Group> publishRank(final Group group, final Rank rank) {
        if (rank.isStatic()) {
            return Futures.immediateFuture(group);
        }

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
    public ListenableFuture<Member> publishRank(final Member member, final Rank rank) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Insert query = queries.getQuery(SQLQueries.INSERT_MEMBER_RANK);
                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
                query.bind(2, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();
                return member;
            }
        });
    }

    @Override
    public ListenableFuture<Member> dropRank(final Member member, final Rank rank) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Query query;
                query = queries.getQuery(SQLQueries.DROP_MEMBER_RANK);
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
                Query query = queries.getQuery(SQLQueries.DROP_RANK_IN_SOCIETIES);
                query.bind(1, UUIDGen.toByteArray(rank.getUUID()));
                query.execute();

                query = queries.getQuery(SQLQueries.DROP_RANK_IN_MEMBERS);
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
