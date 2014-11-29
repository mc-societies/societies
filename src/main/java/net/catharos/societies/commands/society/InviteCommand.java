package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.bridge.Player;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.request.*;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.shank.config.ConfigSetting;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.request.ChoiceRequestMessenger;
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
    Member target;

    private final boolean trustDefault;
    private final Rank normalDefaultRank;
    private final RequestFactory<Choices> requests;
    private final int maxSize;

    @InjectLogger
    private Logger logger;

    @Inject
    public InviteCommand(@ConfigSetting("trust.trust-members-by-default") boolean trustDefault,
                         @Named("normal-default-rank") Rank normalDefaultRank,
                         RequestFactory<Choices> requests,
                         @ConfigSetting("society.max-size") int maxSize) {
        this.trustDefault = trustDefault;
        this.normalDefaultRank = normalDefaultRank;
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

        if (target.hasGroup()) {
            sender.send("target-member.already-member");
            return;
        }

        if (maxSize >= 0 && group.size() >= maxSize) {
            sender.send("society.reached-max-size");
            return;
        }

        if (!target.get(Player.class).isAvailable()) {
            sender.send("target-member.not-available");
            return;
        }

        Request<Choices> request = requests
                .create(sender, new SingleInvolved(target), new InviteRequestMessenger(sender, group, target));

        if (!request.start()) {
            sender.send("requests.participants-not-ready");
            return;
        }

        addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
                if (result == null) {
                    return;
                }

                if (result.getChoice().success()) {
                    group.addMember(target);

                    if (trustDefault) {
                        target.addRank(normalDefaultRank);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                logger.catching(t);
            }
        });
    }

    private static class InviteRequestMessenger extends ChoiceRequestMessenger {

        private final Member initiator;
        private final Group group;
        private final Member target;

        private InviteRequestMessenger(Member initiator, Group group, Member target) {
            this.initiator = initiator;
            this.group = group;
            this.target = target;
        }

        @Override
        public void start(Request<Choices> request) {
            request.getSupplier().send("invite.started", target.getName());
            super.start(request);
        }

        @Override
        public void start(Request<Choices> request, Participant participant) {
            participant.send("invite.member-invites", initiator.getName(), group.getTag());
        }

        @Override
        public void end(Request<Choices> request, Choices choice) {

            if (choice.success()) {
                request.getSupplier().send("member-joined", target.getName());
            } else {
                request.getSupplier().send("member-failed", target.getName());
            }

            super.end(request, choice);
        }

        @Override
        public void end(Participant participant, Request<Choices> request, Choices choice) {
            if (choice.success()) {
                participant.send("you-joined", group.getTag());
            } else {
                participant.send("you-failed", group.getTag());
            }
        }
    }
}
