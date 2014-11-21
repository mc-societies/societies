package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.Members;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.request.*;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.request.ChoiceRequestMessenger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a AbandonCommand
 */
@Command(identifier = "command.join", async = true)
@Permission("societies.join")
@Sender(value = Member.class)
public class JoinCommand implements Executor<Member> {

    @Argument(name = "argument.target.society")
    Group target;

    private final boolean trustDefault;
    private final Rank normalDefaultRank;
    private final RequestFactory<Choices> requests;
    private final int maxSize;

    @InjectLogger
    private Logger logger;


    @Inject
    public JoinCommand(@Named("trust.trust-members-by-default") boolean trustDefault,
                       @Named("normal-default-rank") Rank normalDefaultRank,
                       RequestFactory<Choices> requests, @Named("society.max-size") int maxSize) {
        this.trustDefault = trustDefault;
        this.normalDefaultRank = normalDefaultRank;
        this.requests = requests;
        this.maxSize = maxSize;
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {
        if (maxSize >= 0 && target.size() >= maxSize) {
            sender.send("society.other-reached-max-size", target.getName());
            return;
        }

        Set<Member> participants = target.getMembers("vote.join");
        int online = Members.onlineMembers(participants);

        if (online < 1) {
            sender.send("participants.not-available");
            return;
        }

        final Request<Choices> request = requests
                .create(sender, new SetInvolved(participants), new JoinRequestMessenger(target));
        request.start();

        addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
            @Override
            public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
                if (result == null) {
                    return;
                }

                if (result.getChoice().success()) {
                    target.addMember(sender);

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

    private static class JoinRequestMessenger extends ChoiceRequestMessenger {

        private final Group group;

        private JoinRequestMessenger(Group group) {this.group = group;}


        @Override
        public void start(Request<Choices> request) {
            request.getSupplier().send("join.started", group.getTag());
            super.start(request);
        }

        @Override
        public void start(Request<Choices> request, Participant participant) {
            participant.send("join.member-requests", participant.getName());
        }

        @Override
        public void end(Request<Choices> request, Choices choice) {
            if (choice.success()) {
                request.getSupplier().send("you-joined");
            } else {
                request.getSupplier().send("you-failed");
            }

            super.end(request, choice);
        }

        @Override
        public void end(Participant participant, Request<Choices> request, Choices choice) {
            if (choice.success()) {
                participant.send("member-joined", participant.getName());
            } else {
                participant.send("member-failed", participant.getName());
            }


        }
    }
}
