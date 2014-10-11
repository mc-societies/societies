package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberGroupPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.sql.layout.tables.records.MembersRecord;
import org.jooq.Update;

import java.util.UUID;

/**
 * Represents a SocietyModifier
 */
class SQLMemberGroupPublisher extends AbstractPublisher implements MemberGroupPublisher {

    @Inject
    public SQLMemberGroupPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public void publish(Member member, Group group) {
        Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_SOCIETY);

        UUID uuid = null;

        if (group != null) {
            uuid = group.getUUID();
        }

        query.bind(1, UUIDGen.toByteArray(uuid));
        query.bind(2, UUIDGen.toByteArray(member.getUUID()));

        query.execute();
    }
}
