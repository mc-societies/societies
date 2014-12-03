package org.societies.commands.society.relation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.shank.config.ConfigSetting;
import org.shank.logging.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.societies.api.Members;
import org.societies.commands.RuleStep;
import org.societies.commands.VerifyStep;
import org.societies.groups.Relation;
import org.societies.groups.RelationFactory;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.Member;
import org.societies.groups.request.*;
import org.societies.groups.request.simple.Choices;
import org.societies.request.ChoiceRequestMessenger;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.allies")
@Permission("societies.allies.list")
@Children({
        AlliesCommand.AddCommand.class,
        AlliesCommand.RemoveCommand.class
})
@Meta({@Entry(key = RuleStep.RULE, value = "allies.list"), @Entry(key = VerifyStep.VERIFY)})
@Sender(Member.class)
public class AlliesCommand extends ListCommand {

    //================================================================================
    // List
    //================================================================================

    public static final Relation.Type TYPE = Relation.Type.ALLIED;

    @Inject
    public AlliesCommand(Provider<Table> tableProvider, GroupProvider groupProvider) {
        super(tableProvider, groupProvider);
    }

    @Override
    protected Relation.Type getType() {
        return TYPE;
    }

    //================================================================================
    // Remove
    //================================================================================

    @Command(identifier = "command.allies.remove", async = true)
    @Permission("societies.allies.remove")
    @Sender(Member.class)
    @Meta({@Entry(key = RuleStep.RULE, value = "allies.remove"), @Entry(key = VerifyStep.VERIFY)})
    public static class RemoveCommand implements Executor<Member> {

        @Argument(name = "argument.target.society")
        Group target;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            group.removeRelation(target);

            sender.send("allies.removed", target.getName());

            for (Member member : target.getMembers()) {
                member.send("allies.ended", group.getTag());
            }

            for (Member member : group.getMembers()) {
                member.send("allies.ended", target.getTag());
            }
        }
    }

    //================================================================================
    // Add
    //================================================================================

    @Command(identifier = "command.allies.add", async = true)
    @Sender(Member.class)
    @Meta({@Entry(key = RuleStep.RULE, value = "allies.add"), @Entry(key = VerifyStep.VERIFY)})
    @Permission("societies.allies.add")
    public static class AddCommand implements Executor<Member> {

        @Argument(name = "argument.target.society")
        Group target;

        private final int minSize;
        private final RelationFactory factory;
        private final RequestFactory<Choices> requests;

        @InjectLogger
        private Logger logger;

        @Inject
        public AddCommand(@ConfigSetting("relations.min-size-to-set-ally") int minSize,
                          RelationFactory factory,
                          RequestFactory<Choices> requests) {
            this.minSize = minSize;
            this.factory = factory;
            this.requests = requests;
        }

        @Override
        public void execute(CommandContext<Member> ctx, final Member sender) {
            final Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            if (group.hasRelation(target)) {
                sender.send("socity.already-relation");
                return;
            }

            if (group.size() < minSize) {
                sender.send("society.too-small");
                return;
            }

            Set<Member> participants = Members.onlineMembers(target.getMembers("vote.allies"));

            if (participants.size() < 1) {
                sender.send("target-participants.not-available");
                return;
            }

            SetInvolved involved = new SetInvolved(participants);
            Request<Choices> request = requests.create(sender, involved, new AlliesRequestMessenger(group, target));
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
                        Relation relation = factory.create(group, target, TYPE);
                        group.setRelation(target, relation);
                    }
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    logger.catching(t);
                }
            });
        }

        private static class AlliesRequestMessenger extends ChoiceRequestMessenger {

            private final Group initiator;
            private final Group opponent;

            private AlliesRequestMessenger(Group initiator, Group opponent) {
                this.initiator = initiator;
                this.opponent = opponent;
            }

            @Override
            public void start(Request<Choices> request) {
                request.getSupplier().send("requests.allies.asked-start", opponent.getTag());
                super.start(request);
            }

            @Override
            public void start(Request<Choices> request, Participant participant) {
                participant.send("requests.allies.ask-start", initiator.getTag());
            }

            @Override
            public void end(Request<Choices> request, Choices choice) {
                if (choice.success()) {
//                    request.getSupplier().send("requests.allies.started", opponent.getTag());
                } else {
                    request.getSupplier().send("requests.allies.failed", opponent.getTag());
                }

                super.end(request, choice);
            }

            @Override
            public void end(Participant participant, Request<Choices> request, Choices choice) {

                if (choice.success()) {
//                    participant.send("requests.allies.started", initiator.getTag());

                    for (Member member : opponent.getMembers()) {
                        member.send("requests.allies.started", initiator.getTag());
                    }

                    for (Member member : initiator.getMembers()) {
                        member.send("requests.allies.started", opponent.getTag());
                    }

                } else {
                    participant.send("requests.allies.failed", initiator.getTag());
                }
            }
        }
    }
}
