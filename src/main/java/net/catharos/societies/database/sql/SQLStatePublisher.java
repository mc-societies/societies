package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.ListenableFuture;
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

import java.util.concurrent.Callable;

/**
 * Represents a SQLGroupStatePublisher
 */
class SQLStatePublisher extends AbstractPublisher implements GroupStatePublisher, MemberStatePublisher {

    @Inject
    public SQLStatePublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Group> publishState(final Group group, final short state) {

        return service.submit(new Callable<Group>() {
            @Override
            public Group call() throws Exception {
                Update<SocietiesRecord> query = queries.getQuery(SQLQueries.UPDATE_SOCIETY_STATE);

                query.bind(1, state);
                query.bind(2, UUIDGen.toByteArray(group.getUUID()));

                query.execute();

                return group;
            }
        });
    }

    @Override
    public <M extends Member> ListenableFuture<M> publishState(final M member, final short state) {
        return service.submit(new Callable<M>() {
            @Override
            public M call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_STATE);

                query.bind(1, state);
                query.bind(2, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
                return member;
            }
        });
    }
}
