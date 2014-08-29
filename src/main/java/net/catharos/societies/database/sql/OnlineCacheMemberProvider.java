package net.catharos.societies.database.sql;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Member;
import net.catharos.groups.MemberProvider;
import net.catharos.societies.PlayerProvider;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
class OnlineCacheMemberProvider<M extends Member> implements MemberProvider<M> {

    //todo clean on player disconnect
    private final THashMap<UUID, M> member = new THashMap<UUID, M>();

    private final MemberProvider<M> forward;
    private final PlayerProvider provider;

    @Inject
    public OnlineCacheMemberProvider(@Named("forward") MemberProvider<M> forward, PlayerProvider provider) {
        this.forward = forward;
        this.provider = provider;
    }


    @Override
    public ListenableFuture<M> getMember(UUID uuid) {
        Player player = provider.getPlayer(uuid);

        return handle(player, uuid);
    }

    public ListenableFuture<M> handle(Player player, UUID uuid) {

        ListenableFuture<M> future = null;

        if (player != null) {
            M societyMember = member.get(uuid);

            if (societyMember != null) {
                return Futures.immediateFuture(societyMember);
            }


            future = forward.getMember(uuid);

            Futures.addCallback(future, new FutureCallback<M>() {
                @Override
                public void onSuccess(M result) {
                    member.put(result.getUUID(), result);
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        }

        if (future == null) {
            future = forward.getMember(uuid);
        }

        return future;
    }

    @Override
    public ListenableFuture<M> getMember(String name) {
        Player player = provider.getPlayer(name);
        return handle(player, player.getUniqueId());
    }
}
