package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.publisher.GroupPublisher;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.member.SocietyMember;

import static org.bukkit.ChatColor.stripColor;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

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
    private final double price;

    @Inject
    public CreateCommand(GroupFactory groupFactory,
                         GroupPublisher publisher,
                         NameValidator nameValidator, TagValidator tagValidator,
                         Config config) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
        this.nameValidator = nameValidator;
        this.tagValidator = tagValidator;
        this.price = config.getDouble("economy.creation-price");
    }

    @Override
    public void execute(CommandContext<Sender> ctx, final Sender sender) {
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

        if (!sender.as(new SenderWithdrawer(), SocietyMember.class)) {
            sender.send("economy.not-enough-money");
            return;
        }

        name = stripColor(name).trim();
        tag = translateAlternateColorCodes('&', tag);

        Group group = groupFactory.create(name, tag);
        ListenableFuture<Group> future = publisher.publish(group);

        Futures.addCallback(future, new FutureCallback<Group>() {
            @Override
            public void onSuccess(Group result) {
                if (result == null) {
                    sender.send("society.already-exists", name, tag);
                    return;
                }

                if (sender instanceof SocietyMember) {
                    result.addMember(((SocietyMember) sender));
                }

                sender.send("society.created", name, tag);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });


    }

    private class SenderWithdrawer implements Sender.Executor<SocietyMember, Boolean> {
        @Override
        public Boolean execute(SocietyMember sender) {return sender.withdraw(price).transactionSuccess(); }

        @Override
        public Boolean defaultValue(Sender sender) {
            return true;
        }
    }
}
