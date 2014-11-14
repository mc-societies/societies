package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.request.DefaultRequestResult;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestFactory;
import net.catharos.groups.request.SingleInvolved;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.i18n.Dictionary;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.member.SocietyMember;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a InviteCommand
 */
@Command(identifier = "command.invite", async = true)
@Permission("societies.invite")
@Meta(@Entry(key = RuleStep.RULE, value = "invite"))
@Sender(Member.class)
public class InviteCommand implements Executor<Member> {

    @Argument(name = "argument.target.member")
    SocietyMember target;

    public static final String FAILED = "Invite failed! %s";

    private final Dictionary<String> dictionary;
    private final RequestFactory<Choices> requests;
    private final int maxSize;

    @InjectLogger
    private Logger logger;

    @Inject
    public InviteCommand(Dictionary<String> dictionary, RequestFactory<Choices> requests, @Named("society.max-size") int maxSize) {
        this.dictionary = dictionary;
        this.requests = requests;
        this.maxSize = maxSize;
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {
        final Group group = sender.getGroup();

        if (group == null) {
            sender.send("society.not-found");
            return;
        }

        if (maxSize >= 0 && group.size() >= maxSize) {
            sender.send("society.reached-max-size");
            return;
        }

        String name = dictionary.getTranslation("requests.invite", new Object[]{sender.getName(), group.getName()});
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
                        group.addMember(target);
                        target.send("You've been added to {0}!", group.getName());
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
                logger.catching(t);
            }
        });
    }
}
