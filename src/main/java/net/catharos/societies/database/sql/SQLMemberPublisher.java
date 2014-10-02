package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.MemberPublisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.Insert;

import java.util.concurrent.Callable;

/**
 * Represents a SQLMemberPublisher
 */
class SQLMemberPublisher<M extends Member> extends AbstractPublisher implements MemberPublisher<M> {

    @Inject
    public SQLMemberPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<M> publish(final M member) {
        return service.submit(new Callable<M>() {
            @Override
            public M call() throws Exception {
                Insert<MembersRecord> query = queries.getQuery(SQLQueries.INSERT_MEMBER);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                Object group = null;

                if (member.getGroup() != null) {
                    group = UUIDGen.toByteArray(member.getGroup().getUUID());
                }
                query.bind(2, group);

                query.execute();

                return member;
            }
        });
    }

}
