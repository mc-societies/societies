package org.societies.member;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import org.jetbrains.annotations.Nullable;
import org.societies.groups.member.Member;
import org.societies.groups.member.MemberProvider;

/**
 * Represents a SenderAdapter
 */
class SenderAdapter implements SenderProvider {

    private final MemberProvider memberProvider;

    @Inject
    public SenderAdapter(MemberProvider memberProvider) {this.memberProvider = memberProvider;}

    @Nullable
    @Override
    public Optional<Sender> getSender(String name) {
        Optional<Member> member = memberProvider.getMember(name);

        if (member.isPresent()) {
            return Optional.<Sender>of(member.get());
        }

        return Optional.absent();
    }
}
