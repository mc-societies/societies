package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import net.catharos.groups.Group;
import net.catharos.groups.request.SimpleRequest;
import net.catharos.groups.request.SimpleRequestMessenger;
import net.catharos.groups.request.SimpleRequestResult;
import net.catharos.groups.request.SingleInvolved;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a InviteCommand
 */
@Command(identifier = "command.invite")
//todo simplify requests
public class InviteCommand implements Executor<SocietyMember> {

    @Argument(name = "argument.member.target")
    SocietyMember target;

    public static final String FAILED = "Invite failed! %s";

    @Override
    public void execute(CommandContext<SocietyMember> ctx, final SocietyMember sender) {
        SimpleRequest request = new SimpleRequest(new SimpleRequestMessenger(), new SingleInvolved(target));
        request.start();

        addCallback(request.result(), new FutureCallback<SimpleRequestResult<SimpleRequest.Choices>>() {
            @Override
            public void onSuccess(@Nullable SimpleRequestResult<SimpleRequest.Choices> result) {
                if (result == null) {
                    return;
                }

                switch (result.getChoice()) {
                    case ACCEPT:
                        Group group = sender.getGroup();
                        group.addMember(target);
                        target.send("You've been added to %s!", group.getName());
                        sender.send(target.getName() + " is not a member of your society!");
                        break;
                    case DENY:
                    case ABSTAIN:
                        target.send(FAILED);
                        sender.send(FAILED);
                        break;
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                target.send(FAILED, t.getMessage());
                sender.send(FAILED, t.getMessage());
            }
        });
    }
}
