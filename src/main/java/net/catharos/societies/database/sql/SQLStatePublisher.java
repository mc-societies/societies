package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.GroupStatePublisher;
import net.catharos.groups.publisher.MemberStatePublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.sql.layout.tables.records.MembersRecord;
import net.catharos.societies.database.sql.layout.tables.records.SocietiesRecord;
import org.jooq.Update;

/**
 * Represents a SQLGroupStatePublisher
 */
class SQLStatePublisher extends AbstractPublisher implements GroupStatePublisher, MemberStatePublisher {

    @Inject
    public SQLStatePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Group group, short state) {
        Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_STATE);

        query.bind(1, state);
        query.bind(2, UUIDGen.toByteArray(group.getUUID()));

        query.execute();
    }

    @Override
    public void publish(Member member, short state) {
        Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_STATE);

        query.bind(1, state);
        query.bind(2, UUIDGen.toByteArray(member.getUUID()));

        query.execute();
    }
}
