package net.catharos.societies.member;

import com.google.inject.Inject;
import net.catharos.groups.MemberCache;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.SocietiesQueries;
import net.catharos.societies.cache.Cache;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Result;
import org.jooq.Select;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Represents a MemberCache
 */
public class LoadingMemberCache extends Cache<SocietyMember> implements MemberCache, SenderProvider {

    public static final int MAX_CACHED = 250;

    public static final int MEMBER_LIFE_TIME = 2;

    private final SocietiesQueries queries;
    private final PlayerProvider<Player> playerProvider;
    private final MemberFactory factory;

    @Inject
    public LoadingMemberCache(SocietiesQueries queries, PlayerProvider<Player> playerProvider, MemberFactory factory) {
        super(MAX_CACHED, MEMBER_LIFE_TIME, TimeUnit.HOURS);
        this.queries = queries;
        this.playerProvider = playerProvider;
        this.factory = factory;
    }

    @Override
    public SocietyMember getMember(UUID uuid) {
        return get(uuid);
    }

    @Override
    public SocietyMember getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player.getUniqueId());
    }

    @Override
    public SocietyMember load(@NotNull UUID uuid) throws Exception {
        // Select record from database
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
            return null; //fixme create new member
        } else if (result.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        // create core account object
        MembersRecord record = result.get(0);

        return factory.create(UUIDGen.toUUID(record.getUuid()));
    }

    @Nullable
    @Override
    public Sender getSender(String name) {
        return getMember(name);
    }

    @Nullable
    @Override
    public Sender getSender(UUID uuid) {
        return getMember(uuid);
    }
}
