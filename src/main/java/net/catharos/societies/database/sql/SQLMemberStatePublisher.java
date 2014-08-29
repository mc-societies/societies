package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberStatePublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.Update;

/**
 * Represents a SQLMemberStatePublisher
 */
class SQLMemberStatePublisher extends AbstractPublisher implements MemberStatePublisher {

    @Inject
    public SQLMemberStatePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Member member, short state) {
        Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_SOCIETY);

        query.bind(1, state);
        query.bind(2, UUIDGen.toByteArray(member.getUUID()));

        query.execute();
    }
}
