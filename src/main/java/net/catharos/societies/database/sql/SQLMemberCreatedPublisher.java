package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberCreatedPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.sql.layout.tables.records.MembersRecord;
import org.joda.time.DateTime;
import org.jooq.Update;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * Represents a SocietyNameUpdater
 */
class SQLMemberCreatedPublisher extends AbstractPublisher implements MemberCreatedPublisher {

    @Inject
    public SQLMemberCreatedPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishCreated(final M member, final DateTime created) {
        return service.submit(new Callable<M>() {
            @Override
            public M call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_CREATED);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();
                return member;
            }
        });
    }
}
