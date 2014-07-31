package net.catharos.societies.member.sql;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.MemberProvider;
import net.catharos.groups.MemberPublisher;
import net.catharos.lib.core.util.ByteUtil;
import net.catharos.lib.core.uuid.UUIDGen;
import net.catharos.societies.PlayerProvider;
import net.catharos.societies.database.layout.tables.records.MembersRecord;
import net.catharos.societies.member.MemberException;
import net.catharos.societies.member.MemberFactory;
import net.catharos.societies.member.SocietyMember;
import org.bukkit.entity.Player;
import org.jooq.Insert;
import org.jooq.Query;
import org.jooq.Result;
import org.jooq.Select;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Represents a LoadingMemberProvider
 */
public class SQLMemberController implements MemberProvider<SocietyMember>, MemberPublisher<SocietyMember> {

    private final PlayerProvider playerProvider;
    private final MemberQueries queries;
    private final ListeningExecutorService service;
    private final MemberFactory factory;
    private final GroupProvider groupProvider;

    @Inject
    public SQLMemberController(PlayerProvider playerProvider,
                               MemberQueries queries,
                               ListeningExecutorService service,
                               MemberFactory memberFactory,
                               GroupProvider groupProvider) {
        this.playerProvider = playerProvider;
        this.queries = queries;
        this.service = service;
        this.factory = memberFactory;
        this.groupProvider = groupProvider;
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(final UUID uuid) {
        Select<MembersRecord> query = queries.getQuery(MemberQueries.SELECT_MEMBER_BY_UUID);
        query.bind(1, ByteUtil.toByteArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()));

        ListenableFuture<Result<MembersRecord>> future = queries.query(service, query);

        return Futures.transform(future, new Function<Result<MembersRecord>, SocietyMember>() {
            @Nullable
            @Override
            public SocietyMember apply(@Nullable Result<MembersRecord> input) {
                if (input == null) {
                    return null;
                }

                if (input.isEmpty()) {
                    return factory.create(uuid);
                } else if (input.size() > 1) {
                    throw new MemberException(uuid, "There are more users with the same uuid?!");
                }

                // create core account object
                MembersRecord record = input.get(0);

                SocietyMember member = factory.create(UUIDGen.toUUID(record.getUuid()));

                UUID uuid = UUIDGen.toUUID(record.getSociety());

                // Get society
                try {
                    member.setGroup(groupProvider.getGroup(uuid).get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return member;
            }
        });
    }

    @Override
    public ListenableFuture<SocietyMember> getMember(String name) {
        Player player = playerProvider.getPlayer(name);

        if (player == null) {
            return null;
        }

        return getMember(player.getUniqueId());
    }

    @Override
    public ListenableFuture<SocietyMember> publish(final SocietyMember member) {
        return service.submit(new Callable<SocietyMember>() {
            @Override
            public SocietyMember call() throws Exception {
                Insert<MembersRecord> query = queries.getQuery(MemberQueries.INSERT_MEMBER);

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

    @Override
    public ListenableFuture<?> drop(final SocietyMember member) {
        return service.submit(new Runnable() {
            @Override
            public void run() {
                Query query = queries.getQuery(MemberQueries.DROP_MEMBER_BY_UUID);

                query.bind(1, UUIDGen.toByteArray(member.getUUID()));

                query.execute();
            }
        });
    }
}
