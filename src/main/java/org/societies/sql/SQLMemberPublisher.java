package org.societies.sql;

import com.google.inject.Inject;
import org.jooq.Insert;
import org.jooq.Query;
import org.societies.database.sql.layout.tables.records.MembersRecord;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberPublisher;

/**
 * Represents a SQLMemberPublisher
 */
class SQLMemberPublisher implements MemberPublisher {

    private final Queries queries;

    @Inject
    public SQLMemberPublisher(Queries queries) {
        this.queries = queries;
    }

    @Override
    public Member publish(final Member member) {
        Insert<MembersRecord> query = queries.getQuery(Queries.INSERT_MEMBER);

        query.bind(1, member.getUUID());

        query.execute();
        return member;

    }

    @Override
    public Member destruct(final Member member) {
        Query query = queries.getQuery(Queries.DROP_MEMBER_BY_UUID);

        query.bind(1, member.getUUID());

        query.execute();
        return member;
    }
}
