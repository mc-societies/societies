package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Update;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.MemberGroupPublisher;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a SocietyModifier
 */
class SQLMemberGroupPublisher extends AbstractPublisher implements MemberGroupPublisher {

    @Inject
    public SQLMemberGroupPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> publishGroup(final Member member, final Group group) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_SOCIETY);

                UUID uuid = null;

                if (group != null) {
                    uuid = group.getUUID();
                }

                query.bind(1, UUIDGen.toByteArray(uuid));
                query.bind(2, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
