package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.joda.time.DateTime;
import org.jooq.Update;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.MemberCreatedPublisher;

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
    public ListenableFuture<Member> publishCreated(final Member member, final DateTime created) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_CREATED);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));
                query.bind(2, new Timestamp(created.getMillis()));

                query.execute();
                return member;
            }
        });
    }
}