package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupPublisher;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;

import java.util.concurrent.ExecutionException;

/**
* Represents a CreateCommand
*/
@Command(identifier = "command.create")
public class CreateCommand implements Executor<Sender> {


    @Argument(name = "argument.society.name")
    String name;

    private final GroupFactory groupFactory;
    private final GroupPublisher publisher;

    @Inject
    public CreateCommand(GroupFactory groupFactory, GroupPublisher publisher) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        Group group = groupFactory.create(name);
        ListenableFuture<Group> future = publisher.publish(group);

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        sender.send("society.created", name, name);
    }
}
