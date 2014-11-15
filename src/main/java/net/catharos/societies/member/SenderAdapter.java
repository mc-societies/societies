package net.catharos.societies.member;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import net.catharos.societies.api.member.SocietyMember;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a SenderAdapter
 */
class SenderAdapter implements SenderProvider {

    private final MemberProvider<SocietyMember> memberProvider;

    @Inject
    public SenderAdapter(MemberProvider<SocietyMember> memberProvider) {this.memberProvider = memberProvider;}

    @Nullable
    @Override
    public Sender getSender(String name) {
        return Futures.getUnchecked(memberProvider.getMember(name));
    }
}
