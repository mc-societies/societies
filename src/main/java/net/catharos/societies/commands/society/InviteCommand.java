package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.request.DefaultRequestResult;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestFactory;
import net.catharos.groups.request.SingleInvolved;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a InviteCommand
 */
@Command(identifier = "command.invite")
@Sender(Member.class)
public class InviteCommand implements Executor<Member> {

    @Argument(name = "argument.target.member")
    SocietyMember target;

    public static final String FAILED = "Invite failed! %s";

    private final Dictionary<String> dictionary;
    private final RequestFactory<Choices> requests;


    @Inject
    public InviteCommand(Dictionary<String> dictionary, RequestFactory<Choices> requests) {
        this.dictionary = dictionary;
        this.requests = requests;
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {
        String name = dictionary.getTranslation("requests.invite", sender.getName(), sender.getGroup().getName());
        Request<Choices> request = requests.create(sender, name, new SingleInvolved(target));
        request.start();

        addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
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
                t.printStackTrace();
            }
        });
    }
}
