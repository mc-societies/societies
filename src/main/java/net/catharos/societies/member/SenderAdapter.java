package net.catharos.societies.member;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Inject;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import org.jetbrains.annotations.Nullable;

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
        return Futures.getUnchecked(memberProvider.getMember(name));
    }
}
