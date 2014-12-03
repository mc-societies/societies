package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Insert;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberPublisher;

import java.util.concurrent.Callable;

/**
 * Represents a SQLMemberPublisher
 */
class SQLMemberPublisher extends AbstractPublisher implements MemberPublisher {

    @Inject
    public SQLMemberPublisher(ListeningExecutorService service, Queries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> publish(final Member member) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Insert<MembersRecord> query = queries.getQuery(Queries.INSERT_MEMBER);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
