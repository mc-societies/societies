package net.catharos.societies.member;

import com.google.common.cache.CacheLoader;
import net.catharos.groups.DefaultMember;
import net.catharos.groups.Member;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.SocietiesQueries;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.jetbrains.annotations.NotNull;
import org.jooq.Result;
import org.jooq.Select;

import java.util.UUID;

/**
 * Represents a MemberCacheLoader
 */
public class MemberCacheLoader extends CacheLoader<UUID, Member> {

    private final SocietiesQueries queries;

    public MemberCacheLoader(SocietiesQueries queries) {this.queries = queries;}

    @Override
    public Member load(@NotNull UUID uuid) throws Exception {

        // Select record from database
        Select<MembersRecord> query = queries.getQuery(SocietiesQueries.SELECT_MEMBER);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        // Check result
        Result<MembersRecord> result;

        try {
            result = query.fetch();
        } catch (RuntimeException e) {
            throw new MemberException(uuid, e, "Database connection failed!");
        }

        if (result.isEmpty()) {
            return null; //fixme create new member
        } else if (result.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        // create core account object
        MembersRecord record = result.get(0);

        return new DefaultMember(UUIDGen.toUUID(record.getUuid()));
    }
}
