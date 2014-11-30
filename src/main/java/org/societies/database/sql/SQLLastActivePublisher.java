package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.joda.time.DateTime;
import org.jooq.Update;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.MemberLastActivePublisher;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * Represents a SocietyNameUpdater
 */
class SQLLastActivePublisher extends AbstractPublisher implements MemberLastActivePublisher {

    @Inject
    public SQLLastActivePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> publishLastActive(final Member member, final DateTime date) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_LAST_ACTIVE);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
                query.bind(2, new Timestamp(date.getMillis()));

                query.execute();
                return member;
            }
        });
    }
}
