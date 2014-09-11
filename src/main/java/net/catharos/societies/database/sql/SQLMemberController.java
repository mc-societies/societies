package net.catharos.societies.database.sql;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.*;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.member.MemberException;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;
import org.jooq.*;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Represents a LoadingMemberProvider
 */
class SQLMemberController implements MemberProvider<SocietyMember>, MemberPublisher<SocietyMember> {

    public static final int PREPARE = DefaultGroup.PREPARE;

    private final PlayerProvider playerProvider;
    private final SQLQueries queries;
    private final ListeningExecutorService service;
    private final MemberFactory<SocietyMember> memberFactory;

    private final SQLGroupController groupController;

    @Inject
    public SQLMemberController(PlayerProvider playerProvider,
                               SQLQueries queries,
                               ListeningExecutorService service,
                               MemberFactory<SocietyMember> memberFactory,
                               SQLGroupController groupController) {
        this.playerProvider = playerProvider;
        this.queries = queries;
        this.service = service;
        this.memberFactory = memberFactory;
        this.groupController = groupController;
    }

    //================================================================================
    // Members
    //================================================================================

    @Override
    public ListenableFuture<SocietyMember> getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return Futures.immediateCheckedFuture(null);
        }

        return getMember(player.getUniqueId());
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(UUID uuid) {
        return getMember(uuid, null);
    }

    public ListenableFuture<SocietyMember> getMember(final UUID uuid, final Group group) {
        return queryMember(uuid, new Function<Result<MembersRecord>, SocietyMember>() {
            @Nullable
            @Override
            public SocietyMember apply(@Nullable Result<MembersRecord> input) {
                return evaluateMember(uuid, group, input);
            }
        });
    }

    private ListenableFuture<SocietyMember> queryMember(UUID uuid, Function<Result<MembersRecord>, SocietyMember> applier) {
        Select<MembersRecord> query = queries.getQuery(SQLQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return Futures.transform(future, applier);
    }

    private SocietyMember evaluateMember(UUID uuid, Group predefined, Result<MembersRecord> input) {
        if (input == null) {
            return null;
        }

        if (input.isEmpty()) {
            return memberFactory.create(uuid);
        } else if (input.size() > 1) {
            throw new MemberException(uuid, "There are more users with the same uuid?!");
        }

        MembersRecord record = input.get(0);

        SocietyMember member = memberFactory.create(UUIDGen.toUUID(record.getUuid()));
        // Preparing
        member.setState(PREPARE);

        try {
            // Load society
            byte[] rawSociety = record.getSociety();

            if (rawSociety != null && rawSociety.length == UUIDGen.UUID_LENGTH) {
                // Load group if necessary
                if (predefined == null) {
                    try {
                        predefined = groupController.getGroup(UUIDGen.toUUID(rawSociety), member).get();
                    } catch (InterruptedException e) {
                        throw new MemberException(uuid, e, "Failed to set group of member!");
                    } catch (ExecutionException e) {
                        throw new MemberException(uuid, e, "Failed to set group of member!");
                    }
                }

                // Load ranks for member (get by group)
                if (predefined != null) {
                    member.setGroup(predefined);

                    //Load ranks
                    Select<Record1<byte[]>> query = queries.getQuery(SQLQueries.SELECT_MEMBER_RANKS);
                    query.bind(1, record.getUuid());

                    for (Record1<byte[]> rankRecord : query.fetch()) {
                        UUID rankUUID = UUIDGen.toUUID(rankRecord.value1());
                        Rank rank = predefined.getRank(rankUUID);

                        if (rank != null) {
                            member.addRank(rank);
                        }
                    }
                }
            }

        } finally {
            // Finished
            member.setState(record.getState());
        }

        return member;
    }

    //================================================================================
    // Publisher
    //================================================================================

    @Override
    public ListenableFuture<SocietyMember> publish(final SocietyMember member) {
        return service.submit(new Callable<SocietyMember>() {
            @Override
            public SocietyMember call() throws Exception {
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

    //================================================================================
    // Drop
    //================================================================================

    @Override
    public ListenableFuture<?> drop(final SocietyMember member) {
        return service.submit(new Runnable() {
            @Override
            public void run() {
                Query query = queries.getQuery(SQLQueries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
            }
        });
    }
}
