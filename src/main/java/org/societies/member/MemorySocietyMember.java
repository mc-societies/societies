package org.societies.member;

import org.societies.api.member.SocietyMember;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberPublisher;

/**
 * Represents a MemorySocietyMember
 */
public class MemorySocietyMember implements SocietyMember {

    private final Member owner;
    private final MemberPublisher memberPublisher;

    private boolean ff;

    public MemorySocietyMember(Member owner, MemberPublisher memberPublisher) {
        this.owner = owner;
        this.memberPublisher = memberPublisher;
    }

    @Override
    public boolean isFriendlyFire() {
        return ff;
    }

    @Override
    public void setFirendlyFire(boolean on) {
        this.ff = on;

        if (owner.linked()) {
            memberPublisher.publish(owner);
        }
    }
}
