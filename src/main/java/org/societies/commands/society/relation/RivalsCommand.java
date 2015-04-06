package org.societies.commands.society.relation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import gnu.trove.set.hash.THashSet;
import order.CommandContext;
import order.Executor;
import order.format.table.Table;
import order.reflect.*;
import order.reflect.instance.Children;
import org.apache.logging.log4j.Logger;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.shank.config.ConfigSetting;
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
import java.util.List;
import java.util.Set;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.rivals")
@Permission("societies.rivals.list")
@Children({
        RivalsCommand.AddCommand.class,
        RivalsCommand.RemoveCommand.class
})
@Meta({@Entry(key = RuleStep.RULE, value = "rivals.list"), @Entry(key = VerifyStep.VERIFY)})
@Sender(Member.class)
public class RivalsCommand extends ListCommand {

    //================================================================================
    // List
    //================================================================================

    public static final Relation.Type TYPE = Relation.Type.RIVALED;

    @Inject
    public RivalsCommand(Provider<Table> tableProvider, GroupProvider groupProvider) {
        super(tableProvider, groupProvider);
    }

    @Override
    protected Relation.Type getType() {
        return TYPE;
    }

    //================================================================================
    // Remove
    //================================================================================

    @Command(identifier = "command.rivals.remove", async = true)
    @Permission("societies.rivals.remove")
    @Meta({@Entry(key = RuleStep.RULE, value = "rivals.remove"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class RemoveCommand implements Executor<Member> {

        @Argument(name = "argument.target.society")
        Group target;

        private final RequestFactory<Choices> requests;

        private final Logger logger;

        @Inject
        public RemoveCommand(RequestFactory<Choices> requests, Logger logger) {
            this.requests = requests;
            this.logger = logger;
        }

        @Override
        public void execute(CommandContext<Member> ctx, final Member sender) {
            final Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Set<Member> participants = Members.onlineMembers(target.getMembers("vote.rivals"));

            if (participants.size() < 1) {
                sender.send("target-participants.not-available");
                return;
            }

            Request<Choices> request = requests
                    .create(sender, new SetInvolved(participants), new RivalsRequestMessenger(group, target));

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
                        group.removeRelation(target);
                    }
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    logger.catching(t);
                }
            });
        }

        private static class RivalsRequestMessenger extends ChoiceRequestMessenger {

            private final Group initiator;
            private final Group opponent;

            private RivalsRequestMessenger(Group initiator, Group opponent) {
                this.initiator = initiator;
                this.opponent = opponent;
            }

            @Override
            public void start(Request<Choices> request) {
                request.getSupplier().send("requests.rivals.asked-end", opponent.getTag());
                super.start(request);
            }

            @Override
            public void start(Request<Choices> request, Participant participant) {
                participant.send("requests.rivals.ask-end", initiator.getTag());
            }

            @Override
            public void end(Request<Choices> request, Choices choice) {
                if (choice.success()) {
//                    request.getSupplier().send("requests.rivals.ended", opponent.getTag());
                } else {
                    request.getSupplier().send("requests.rivals.failed", opponent.getTag());
                }

                super.end(request, choice);
            }

            @Override
            public void end(Participant participant, Request<Choices> request, Choices choice) {

                if (choice.success()) {
//                    participant.send("requests.rivals.ended", initiator.getTag());

                    for (Member member : opponent.getMembers()) {
                        member.send("requests.rivals.ended", initiator.getTag());
                    }

                    for (Member member : initiator.getMembers()) {
                        member.send("requests.rivals.ended", opponent.getTag());
                    }

                } else {
                    participant.send("requests.rivals.failed", initiator.getTag());
                }
            }
        }
    }

    //================================================================================
    // Add
    //================================================================================

    @Command(identifier = "command.rivals.add", async = true)
    @Permission("societies.rivals.add")
    @Meta({@Entry(key = RuleStep.RULE, value = "rivals.add"), @Entry(key = VerifyStep.VERIFY)})
    @Sender(Member.class)
    public static class AddCommand implements Executor<Member> {

        @Argument(name = "argument.target.society")
        Group target;

        private final int minSize;
        private final double rivalsLimit;
        private final GroupProvider groupProvider;
        private final Set<String> unrivable;
        private final RelationFactory factory;

        @Inject
        public AddCommand(@ConfigSetting("relations.min-size-to-set-rival") int minSize,
                          @ConfigSetting("relations.unrivable-societies") List<String> unrivable,
                          @ConfigSetting("relations.rival-limit-percent") int rivalsLimit,
                          GroupProvider groupProvider,
                          RelationFactory factory) {
            this.minSize = minSize;
            this.rivalsLimit = rivalsLimit;
            this.groupProvider = groupProvider;
            this.unrivable = new THashSet<String>(unrivable);
            this.factory = factory;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

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

            if (unrivable.contains(ChatColor.stripColor(target.getTag()))) {
                sender.send("target-society.not-rivable", target.getTag());
                return;
            }

            Integer societies = groupProvider.size();

            if (Math.round(societies * (rivalsLimit / 100)) < group.getRelations().size()) {
                sender.send("society.reached-max-rivals");
                return;
            }

            Relation relation = factory.create(group, target, TYPE);

            group.setRelation(target, relation);

            sender.send("rivals.added", target.getTag());

            for (Member member : target.getMembers()) {
                member.send("rivals.started", group.getTag());
            }

            for (Member member : group.getMembers()) {
                member.send("rivals.started", target.getTag());
            }
        }
    }

}
