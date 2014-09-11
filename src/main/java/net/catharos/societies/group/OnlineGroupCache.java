package net.catharos.societies.group;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import gnu.trove.map.hash.THashMap;
import net.catharos.groups.Group;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Member;

import java.util.Set;
import java.util.UUID;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.Futures.immediateCheckedFuture;

/**
 * Represents a OnlineCacheMemberProvider
 */
@Singleton
public class OnlineGroupCache implements GroupProvider {

    private final THashMap<UUID, Group> groups = new THashMap<UUID, Group>();

    private final GroupProvider forward;

    @Inject
    public OnlineGroupCache(@Named("forward") GroupProvider forward) {
        this.forward = forward;
    }

    @Override
    public ListenableFuture<Group> getGroup(UUID uuid) {
        final Group group = groups.get(uuid);

        if (group != null) {
            return immediateCheckedFuture(group);
        }

        ListenableFuture<Group> groupFuture = forward.getGroup(uuid);

        addCallback(groupFuture, new FutureCallback<Group>() {
            @Override
            public void onSuccess(Group result) {
                if (result == null) {
                    return;
                }

                groups.put(result.getUUID(), result);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return groupFuture;
    }

    @Override
    public ListenableFuture<Set<Group>> getGroup(String name) {
        return null;
    }

    @Override
    public ListenableFuture<Set<Group>> getGroups() {
        return forward.getGroups();
    }

    public void clear(UUID uuid) {
        this.groups.remove(uuid);
    }

    public void clear(Member leaving, Group group) {
        if (group == null) {
            return;
        }

        Set<Member> members = group.getMembers();

        for (Member member : members) {
            if (member.equals(leaving)) {
                continue;
            }

            if (member.isAvailable()) {
                clear(group.getUUID());
            }
        }
    }
}
