package net.catharos.societies.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.Update;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a SocietyModifier
 */
class SocietyPublisher extends AbstractPublisher<Member> {

    @Inject
    public SocietyPublisher(ListeningExecutorService service, SQLQueries queries) {
        super(service, queries);
    }

    @Override
    public ListenableFuture<Member> update(final Member member) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(SQLQueries.UPDATE_MEMBER_SOCIETY);

                UUID uuid = null;

                Group group = member.getGroup();
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
