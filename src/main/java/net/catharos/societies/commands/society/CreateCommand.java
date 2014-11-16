package net.catharos.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.catharos.groups.Group;
import net.catharos.groups.GroupFactory;
import net.catharos.groups.Member;
import net.catharos.groups.publisher.GroupPublisher;
import net.catharos.groups.rank.Rank;
import net.catharos.groups.validate.NameValidator;
import net.catharos.groups.validate.TagValidator;
import net.catharos.groups.validate.ValidateResult;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.shank.logging.InjectLogger;
import net.catharos.societies.api.member.SocietyMember;
import org.apache.logging.log4j.Logger;

import static net.catharos.bridge.ChatColor.stripColor;
import static net.catharos.bridge.ChatColor.translateString;

/**
 * Represents a CreateCommand
 */
@Command(identifier = "command.create")
@Permission("societies.create")
@Sender(Member.class)
public class CreateCommand implements Executor<Member> {

    @Argument(name = "argument.society.name")
    String name;

    @Argument(name = "argument.society.tag")
    String tag;

    private final GroupFactory groupFactory;
    private final GroupPublisher publisher;
    private final NameValidator nameValidator;
    private final TagValidator tagValidator;
    private final double price;
    private final Rank superRank;


    @InjectLogger
    private Logger logger;

    @Inject
    public CreateCommand(GroupFactory groupFactory,
                         GroupPublisher publisher,
                         NameValidator nameValidator, TagValidator tagValidator,
                         Config config,
                         @Named("super-default-rank") Rank superRank) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
        this.nameValidator = nameValidator;
        this.tagValidator = tagValidator;
        this.superRank = superRank;
        this.price = config.getDouble("economy.creation-price");
    }

    @Override
    public void execute(CommandContext<Member> ctx, final Member sender) {

        if (sender.hasGroup()) {
            sender.send("society.already-member");
            return;
        }

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
        tag = translateString('&', tag);

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
                    SocietyMember societyMember = (SocietyMember) sender;

                    result.addMember(societyMember);
                    societyMember.addRank(superRank);
                }

                sender.send("society.created", name, tag);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.catching(t);
            }
        });


    }

    private class SenderWithdrawer implements net.catharos.lib.core.command.sender.Sender.Executor<SocietyMember, Boolean> {
        @Override
        public Boolean execute(SocietyMember sender) {return sender.withdraw(price).transactionSuccess(); }

        @Override
        public Boolean defaultValue(net.catharos.lib.core.command.sender.Sender sender) {
            return true;
        }
    }
}
