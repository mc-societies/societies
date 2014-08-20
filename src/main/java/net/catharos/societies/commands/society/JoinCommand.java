package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.request.SetInvolved;
import net.catharos.groups.request.SimpleRequest;
import net.catharos.groups.request.SimpleRequestMessenger;
import net.catharos.groups.request.SimpleRequestResult;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.join", async = true)
public class JoinCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.society.target")
    Group target;

    @Override
    public void execute(CommandContext<SocietyMember> ctx, final SocietyMember sender) {
        Set<Member> participants = target.getMembers(); //fixme
        SimpleRequest request = new SimpleRequest(new SimpleRequestMessenger(), new SetInvolved(participants));
        request.start();

        addCallback(request.result(), new FutureCallback<SimpleRequestResult<SimpleRequest.Choices>>() {
            @Override
            public void onSuccess(@Nullable SimpleRequestResult<SimpleRequest.Choices> result) {
                if (result == null) {
                    return;
                }

                switch (result.getChoice()) {
                    case ACCEPT:
                        target.addMember(sender);
                        break;
                    case DENY:
                    case ABSTAIN:
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                sender.send("Failed: %s", t.getMessage());
            }
        });
    }
}
