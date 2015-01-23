package org.societies.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Query;
import org.societies.database.sql.AbstractPublisher;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberDestructor;

import java.util.concurrent.Callable;

/**
 * Represents a SQLMemberDropPublisher
 */
class SQLMemberDestructor extends AbstractPublisher implements MemberDestructor {

    @Inject
    public SQLMemberDestructor(ListeningExecutorService service, Queries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> destruct(final Member member) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Query query = queries.getQuery(Queries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
