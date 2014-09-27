package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberCreatedPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.joda.time.DateTime;
import org.jooq.Update;

import java.sql.Timestamp;

/**
 * Represents a SocietyNameUpdater
 */
class SQLMemberCreatedPublisher extends AbstractPublisher implements MemberCreatedPublisher {

    @Inject
    public SQLMemberCreatedPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Member member, DateTime date) {
        Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_CREATED);

        query.bind(1, UUIDGen.toByteArray(member.getUUID()));
        query.bind(2, new Timestamp(date.getMillis()));

        query.execute();
    }
}
