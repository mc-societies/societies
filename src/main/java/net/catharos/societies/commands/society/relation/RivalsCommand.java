package net.catharos.societies.commands.society.relation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import gnu.trove.set.hash.THashSet;
import net.catharos.bridge.ChatColor;
import net.catharos.groups.*;
import net.catharos.groups.request.*;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.api.member.SocietyMember;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.commands.VerifyStep;
import net.catharos.societies.request.ChoiceRequestMessenger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
    public RivalsCommand(Provider<Table> tableProvider, MemberProvider<SocietyMember> memberProvider) {
        super(tableProvider, memberProvider);
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

        @InjectLogger
        private Logger logger;

        @Inject
        public RemoveCommand(RequestFactory<Choices> requests) {this.requests = requests;}

        @Override
        public void execute(CommandContext<Member> ctx, final Member sender) {
            final Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Set<Member> participants = group.getMembers("vote.rivals");
            int online = Members.onlineMembers(participants);

            if (online < 1) {
                sender.send("participants.not-available");
                return;
            }

            Request<Choices> request = requests
                    .create(sender, new SetInvolved(participants), new RivalsRequestMessenger(group, target));
            request.start();

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
                    request.getSupplier().send("requests.rivals.ended", opponent.getTag());
                } else {
                    request.getSupplier().send("requests.rivals.failed", opponent.getTag());
                }

                super.end(request, choice);
            }

            @Override
            public void end(Participant participant, Request<Choices> request, Choices choice) {

                if (choice.success()) {
                    participant.send("requests.rivals.ended", initiator.getTag());
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

        @InjectLogger
        private Logger logger;

        @Inject
        public AddCommand(@Named("relations.min-size-to-set-rival") int minSize,
                          @Named("relations.unrivable-societies") ArrayList unrivable,
                          @Named("relations.rival-limit-percent") double rivalsLimit,
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

            if (group.size() < minSize) {
                sender.send("society.too-small");
                return;
            }

            if (unrivable.contains(ChatColor.stripColor(target.getTag()))) {
                sender.send("target-society.not-rivable", target.getTag());
                return;
            }

            Integer societies;

            try {
                societies = groupProvider.size().get();
            } catch (InterruptedException e) {
                logger.catching(e);
                return;
            } catch (ExecutionException e) {
                logger.catching(e);
                return;
            }

            if (Math.round(societies * (rivalsLimit / 100)) < group.getRelations().size()) {
                sender.send("society.reached-max-rivals");
                return;
            }

            Relation relation = factory.create(group, target, TYPE);

            group.setRelation(target, relation);

            sender.send("rivals.added", target.getName());
        }
    }

}
