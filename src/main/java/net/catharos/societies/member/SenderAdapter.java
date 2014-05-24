package net.catharos.societies.member;

import com.google.inject.Inject;
import net.catharos.groups.MemberProvider;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderProvider;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a SenderAdapter
 */
public class SenderAdapter implements SenderProvider {

    private final MemberProvider<SocietyMember> memberProvider;

    @Inject
    public SenderAdapter(MemberProvider<SocietyMember> memberProvider) {this.memberProvider = memberProvider;}

    @Nullable
    @Override
    public Sender getSender(String name) {
        return memberProvider.getMember(name);
    }

    @Nullable
    @Override
    public Sender getSender(UUID uuid) {
        return memberProvider.getMember(uuid);
    }
}
