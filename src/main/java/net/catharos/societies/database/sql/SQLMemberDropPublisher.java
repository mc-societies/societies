package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberDropPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import org.jooq.Query;

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
    public <M extends Member> ListenableFuture<M> drop(final M member) {
        return service.submit(new Callable<M>() {
            @Override
            public M call() throws Exception {
                Query query = queries.getQuery(SQLQueries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
