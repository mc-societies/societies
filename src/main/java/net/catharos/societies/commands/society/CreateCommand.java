package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.GroupPublisher;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;

import java.util.concurrent.ExecutionException;

/**
 * Represents a CreateCommand
 */
@Command(identifier = "command.create")
public class CreateCommand implements Executor<Sender> {

    @Argument(name = "argument.society.name")
    String name;

    @Argument(name = "argument.society.tag")
    String tag;

    private final GroupFactory groupFactory;
    private final GroupPublisher publisher;
    private final NameValidator nameValidator;
    private final TagValidator tagValidator;

    @Inject
    public CreateCommand(GroupFactory groupFactory, GroupPublisher publisher, NameValidator nameValidator, TagValidator tagValidator) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
        this.nameValidator = nameValidator;
        this.tagValidator = tagValidator;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) {
        ValidateResult nameResult = nameValidator.validateName(name);


        if (nameResult.isFailed()) {
            sender.send(nameResult.getMessage());
            return;
        }

        ValidateResult tagResult = tagValidator.validateTag(tag);

        if (tagResult.isFailed()) {
            sender.send(tagResult.getMessage());
            return;
        }

        Group group = groupFactory.create(name, tag);
        ListenableFuture<Group> future = publisher.publish(group);

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (sender instanceof SocietyMember) {
            group.addMember(((SocietyMember) sender));
        }

        sender.send("society.created", name, tag);
    }
}
