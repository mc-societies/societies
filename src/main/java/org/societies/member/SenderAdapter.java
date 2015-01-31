package org.societies.member;

import com.google.inject.Inject;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import org.jetbrains.annotations.Nullable;
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
    public Sender getSender(String name) {
        return memberProvider.getMember(name);
    }
}
