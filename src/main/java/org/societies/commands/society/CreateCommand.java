package org.societies.commands.society;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.shank.config.ConfigSetting;
import net.catharos.lib.shank.logging.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.societies.api.economy.EconomyParticipant;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupFactory;
import org.societies.groups.member.Member;
import org.societies.groups.publisher.GroupPublisher;
import org.societies.groups.rank.Rank;
import org.societies.groups.validate.NameValidator;
import org.societies.groups.validate.TagValidator;
import org.societies.groups.validate.ValidateResult;

import static org.societies.bridge.ChatColor.stripColor;
import static org.societies.bridge.ChatColor.translateString;

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
    private final Rank defaultRank;
    private final boolean verificationRequired;


    @InjectLogger
    private Logger logger;

    @Inject
    public CreateCommand(GroupFactory groupFactory,
                         GroupPublisher publisher,
                         NameValidator nameValidator, TagValidator tagValidator,
                         Config config,
                         @Named("super-default-rank") Rank superRank,
                         @Named("normal-default-rank") Rank defaultRank,
                         @ConfigSetting("verification-required") boolean verificationRequired) {
        this.groupFactory = groupFactory;
        this.publisher = publisher;
        this.nameValidator = nameValidator;
        this.tagValidator = tagValidator;
        this.superRank = superRank;
        this.defaultRank = defaultRank;
        this.verificationRequired = verificationRequired;
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

        if (sender.has(EconomyParticipant.class)) {
            if (!sender.get(EconomyParticipant.class).withdraw(price).transactionSuccess()) {
                sender.send("economy.not-enough-money");
            }
        }

        name = stripColor(name).trim();
        tag = translateString('&', tag);

        Group group = groupFactory.create(name, tag);

        if (!verificationRequired) {
            group.verify(true);
        }

        ListenableFuture<Group> future = publisher.publish(group);

        Futures.addCallback(future, new FutureCallback<Group>() {
            @Override
            public void onSuccess(Group result) {
                if (result == null) {
                    sender.send("society.already-exists", name, tag);
                    return;
                }

//                if (sender instanceof Member) {
//                    Member societyMember = sender;

                result.addMember(sender);
                sender.addRank(defaultRank);
                sender.addRank(superRank);
//                }

                sender.send("society.created", name, tag);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.catching(t);
            }
        });


    }
}
