package net.catharos.societies.commands.society.relation;

import com.google.common.util.concurrent.FutureCallback;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.groups.RelationFactory;
import net.catharos.groups.request.DefaultRequestResult;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestFactory;
import net.catharos.groups.request.SetInvolved;
import net.catharos.groups.request.simple.Choices;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.commands.RuleStep;
import net.catharos.societies.commands.VerifyStep;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;

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
    public AlliesCommand(Provider<Table> tableProvider) {
        super(tableProvider);
    }

    @Override
    protected Relation.Type getType() {
        return TYPE;
    }

    //================================================================================
    // Remove
    //================================================================================

    @Command(identifier = "command.allies.remove")
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

        private final RelationFactory factory;
        private final RequestFactory<Choices> requests;

        @InjectLogger
        private Logger logger;

        @Inject
        public AddCommand(RelationFactory factory, RequestFactory<Choices> requests) {
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

            Request<Choices> request = requests.create(sender, "requests.allies", new SetInvolved(target.getMembers()));
            request.start();

            addCallback(request.result(), new FutureCallback<DefaultRequestResult<Choices>>() {
                @Override
                public void onSuccess(@Nullable DefaultRequestResult<Choices> result) {
                    if (result == null) {
                        return;
                    }

                    switch (result.getChoice()) {
                        case ACCEPT:
                            Relation relation = factory.create(group, target, TYPE);

                            group.setRelation(target, relation);

                            sender.send("allies.added", target.getName());
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
    }


}
