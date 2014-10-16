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
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.inject.Provider;

import static com.google.common.util.concurrent.Futures.addCallback;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.allies")
@Children({
        AlliesCommand.AddCommand.class,
        AlliesCommand.RemoveCommand.class
})
@Sender(Member.class)
public class AlliesCommand extends ListCommand {

    public static final Relation.Type TYPE = Relation.Type.ALLIED;

    @Inject
    public AlliesCommand(Provider<Table> tableProvider) {
        super(tableProvider);
    }

    @Override
    protected Relation.Type getType() {
        return TYPE;
    }

    @Command(identifier = "command.allies.remove")
    @Sender(Member.class)
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

    @Command(identifier = "command.allies.add", async = true)
    @Sender(Member.class)
    public static class AddCommand implements Executor<Member> {

        @Argument(name = "argument.target.society")
        Group target;

        private final RelationFactory factory;
        private final RequestFactory<Choices> requests;

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

            Request<Choices> request = requests.create(sender, "requests.allies", new SetInvolved(group.getMembers()));
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
                    t.printStackTrace();
                }
            });


        }
    }


}