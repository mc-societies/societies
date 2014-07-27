package net.catharos.societies.member.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.Publisher;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jooq.Update;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a SocietyModifier
 */
class SocietyPublisher implements Publisher<Member> {

    private final ListeningExecutorService service;
    private final MemberQueries queries;

    @Inject
    public SocietyPublisher(ListeningExecutorService service, MemberQueries queries) {
        this.service = service;
        this.queries = queries;
    }

    @Override
    public ListenableFuture<Member> update(final Member member) {
        return service.submit(new Callable<Member>() {
            @Override
            public Member call() throws Exception {
                Update<MembersRecord> query = queries.getQuery(MemberQueries.UPDATE_MEMBER_SOCIETY);

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
