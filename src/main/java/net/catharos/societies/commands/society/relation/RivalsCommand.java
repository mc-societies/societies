package net.catharos.societies.commands.society.relation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import net.catharos.groups.*;
import net.catharos.groups.request.*;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.commands.VerifyStep;
import net.catharos.societies.member.SocietyMember;
import net.catharos.societies.request.ChoiceRequestMessenger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;

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

            Request<Choices> request = requests.create(sender, new SetInvolved(group.getMembers()), new RivalsRequestMessenger());
            request.start();

            addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
                @Override
                public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
                    if (result == null) {
                        return;
                    }

                    switch (result.getChoice()) {
                        case ACCEPT:
                            group.removeRelation(target);
                            sender.send("rivals.removed", target.getName());
                            break;
                        case DENY:
                        case ABSTAIN:
                            break;
                    }
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    logger.catching(t);
                }
            });
        }

        //todo
        private static class RivalsRequestMessenger extends ChoiceRequestMessenger {

            @Override
            public void start(Request<Choices> request, Participant participant) {
                request.getSupplier().send("requests.rivals-started");
                participant.send("requests.rivals");
            }

            @Override
            public void end(Participant participant, Request<Choices> request) {
                participant.send("requests.rivals-end", participant.getName());
            }

            @Override
            public void cancelled(Participant participant, Request<Choices> request) {
                end(participant, request);
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

        private final RelationFactory factory;

        public AddCommand(RelationFactory factory) {this.factory = factory;}

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Relation relation = factory.create(group, target, TYPE);

            group.setRelation(target, relation);

            sender.send("rivals.added", target.getName());
        }
    }

}
