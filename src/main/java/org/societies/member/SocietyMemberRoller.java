package org.societies.member;

import com.google.inject.Inject;
import org.societies.api.member.SocietyMember;
import org.societies.groups.ExtensionRoller;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberPublisher;

/**
 * Represents a SocietyMemberRoller
 */
class SocietyMemberRoller implements ExtensionRoller<Member> {

    private final MemberPublisher memberPublisher;

    @Inject
    SocietyMemberRoller(MemberPublisher memberPublisher) {
        this.memberPublisher = memberPublisher;
    }

    @Override
    public void roll(Member extensible) {
        extensible.add(SocietyMember.class, new MemorySocietyMember(extensible, memberPublisher));
    }
}
