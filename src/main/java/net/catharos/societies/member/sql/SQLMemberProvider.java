package net.catharos.societies.member.sql;

import com.google.inject.Inject;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.member.MemberException;
import net.catharos.societies.member.MemberFactory;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;
import org.jooq.Result;
import org.jooq.Select;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a LoadingMemberProvider
 */
class SQLMemberProvider implements MemberProvider<SocietyMember> {

    private final PlayerProvider<Player> playerProvider;
    private final MemberQueries queries;
    private final MemberFactory factory;
    private final Provider<SocietyMember> memberProvider;

    @Inject
    public SQLMemberProvider(PlayerProvider<Player> playerProvider,
                             MemberQueries queries,
                             MemberFactory memberFactory,
                             Provider<SocietyMember> memberProvider) {
        this.playerProvider = playerProvider;
        this.queries = queries;
        this.factory = memberFactory;
        this.memberProvider = memberProvider;
    }

    @Override
    public SocietyMember getMember(UUID uuid) {
        Select<MembersRecord> query = queries.getQuery(MemberQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        // Check result
        Result<MembersRecord> result;

        try {
            result = query.fetch();
        } catch (RuntimeException e) {
            throw new MemberException(uuid, e, "Query failed to execute!");
        }

        if (result.isEmpty()) {
            return memberProvider.get();
        } else if (result.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        // create core account object
        MembersRecord record = result.get(0);

        return factory.create(UUIDGen.toUUID(record.getUuid()));
    }

    @Override
    public SocietyMember getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player.getUniqueId());
    }
}
