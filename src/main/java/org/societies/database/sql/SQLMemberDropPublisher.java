package org.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Query;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.MemberDropPublisher;

import java.util.concurrent.Callable;

/**
 * Represents a SQLMemberDropPublisher
 */
//todo needed?
class SQLMemberDropPublisher extends AbstractPublisher implements MemberDropPublisher {

    @Inject
    public SQLMemberDropPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> drop(final Member member) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Query query = queries.getQuery(SQLQueries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
