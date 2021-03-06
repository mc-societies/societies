package org.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import order.CommandContext;
import order.Executor;
import order.reflect.*;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.shank.config.ConfigSetting;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;
import org.societies.groups.request.*;
import org.societies.groups.request.simple.Choices;
import org.societies.request.ChoiceRequestMessenger;

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

    private Logger logger;

    @Inject
    public InviteCommand(@ConfigSetting("trust.trust-members-by-default") boolean trustDefault,
                         @Named("normal-default-rank") Rank normalDefaultRank,
                         RequestFactory<Choices> requests,
                         @ConfigSetting("society.max-size") int maxSize, Logger logger) {
        this.trustDefault = trustDefault;
        this.normalDefaultRank = normalDefaultRank;
        this.requests = requests;
        this.maxSize = maxSize;
        this.logger = logger;
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

        if (!target.get(Player.class).isOnline()) {
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
