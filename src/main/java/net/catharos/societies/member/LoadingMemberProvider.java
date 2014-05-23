package net.catharos.societies.member;

import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.SocietiesQueries;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.bukkit.entity.Player;
import org.jooq.Result;
import org.jooq.Select;

import javax.inject.Provider;
import java.util.UUID;

/**
 * Represents a LoadingMemberProvider
 */
public class LoadingMemberProvider implements MemberProvider {

    private final PlayerProvider<Player> playerProvider;
    private final SocietiesQueries queries;
    private final MemberFactory factory;
    private final Provider<SocietyMember> memberProvider;

    public LoadingMemberProvider(PlayerProvider<Player> playerProvider,
                                 SocietiesQueries queries,
                                 MemberFactory memberFactory,
                                 Provider<SocietyMember> memberProvider) {
        this.playerProvider = playerProvider;
        this.queries = queries;
        this.factory = memberFactory;
        this.memberProvider = memberProvider;
    }

    @Override
    public Member getMember(UUID uuid) {
        Select<MembersRecord> query = queries.getQuery(SocietiesQueries.SELECT_MEMBER);
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
    public Member getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player.getUniqueId());
    }
}
