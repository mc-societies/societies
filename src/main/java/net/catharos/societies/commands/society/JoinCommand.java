package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.request.DefaultRequestResult;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestFactory;
import net.catharos.groups.request.SetInvolved;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.core.i18n.Dictionary;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.join", async = true)
@Sender(sender = Member.class)
public class JoinCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    private final Dictionary<String> dictionary;
    private final RequestFactory<Choices> requests;

    public JoinCommand(Dictionary<String> dictionary, RequestFactory<Choices> requests) {
        this.dictionary = dictionary;
        this.requests = requests;
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {
        Set<Member> participants = target.getMembers();
        String name = dictionary.getTranslation("requests.join", sender.getName());
        Request<Choices> request = requests.create(sender, name, new SetInvolved(participants));
        request.start();

        addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
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
                t.printStackTrace();
            }
        });
    }
}
