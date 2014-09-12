package net.catharos.societies.member;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.Futures.immediateFuture;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineMemberCache<M extends Member> implements MemberProvider<M> {

    private final THashMap<UUID, M> members = new THashMap<UUID, M>();

    private final MemberProvider<M> forward;
    private final PlayerProvider provider;

    @Inject
    public OnlineMemberCache(@Named("forward") MemberProvider<M> forward, PlayerProvider provider) {
        this.forward = forward;
        this.provider = provider;
    }

    @Override
    public ListenableFuture<M> getMember(UUID uuid) {
        Player player = provider.getPlayer(uuid);
        return handle(player, uuid);
    }

    @Override
    public ListenableFuture<M> getMember(UUID uuid, Group predefined, ListeningExecutorService service) {
        return forward.getMember(uuid, predefined, service);
    }

    public ListenableFuture<M> handle(Player player, UUID uuid) {
        if (player == null) {
            return forward.getMember(uuid);
        }

        M societyMember = members.get(uuid);

        if (societyMember != null) {
            return immediateFuture(societyMember);
        }

        ListenableFuture<M> future = forward.getMember(uuid);

        addCallback(future, new FutureCallback<M>() {
            @Override
            public void onSuccess(M result) {
                members.put(result.getUUID(), result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return future;
    }

    @Override
    public ListenableFuture<M> getMember(String name) {
        Player player = provider.getPlayer(name);
        return handle(player, player.getUniqueId());
    }

    public M clear(UUID uuid) {
        return this.members.remove(uuid);
    }
}
