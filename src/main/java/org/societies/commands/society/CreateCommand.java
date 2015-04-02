package org.societies.commands.society;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.reflect.Sender;
import org.shank.config.ConfigSetting;
import org.societies.api.economy.EconomyParticipant;
import org.societies.api.group.Society;
import org.societies.groups.group.Group;
import org.societies.groups.group.GroupPublisher;
import org.societies.groups.member.Member;
import org.societies.groups.rank.Rank;
import org.societies.groups.validate.NameValidator;
import org.societies.groups.validate.TagValidator;
import org.societies.groups.validate.ValidateResult;

import static org.societies.bridge.ChatColor.stripColor;
import static org.societies.bridge.ChatColor.translateString;

/**
 * Represents a CreateCommand
 */
@Command(identifier = "command.create", async = true)
@Permission("societies.create")
@Sender(Member.class)
public class CreateCommand implements Executor<Member> {

    @Argument(name = "argument.society.name")
    String name;

    @Argument(name = "argument.society.tag")
    String tag;

    private final GroupPublisher publisher;
    private final NameValidator nameValidator;
    private final TagValidator tagValidator;
    private final double price;
    private final Rank superRank;
    private final Rank defaultRank;
    private final boolean verificationRequired;

    @Inject
    public CreateCommand(GroupPublisher publisher,
                         NameValidator nameValidator, TagValidator tagValidator,
                         Config config,
                         @Named("super-default-rank") Rank superRank,
                         @Named("normal-default-rank") Rank defaultRank,
                         @ConfigSetting("verification-required") boolean verificationRequired) {
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

        if (sender.has(EconomyParticipant.class) && price > 0) {
            if (!sender.get(EconomyParticipant.class).withdraw(price).transactionSuccess()) {
                sender.send("economy.not-enough-money");
                return;
            }
        }

        name = stripColor(name).trim();
        tag = translateString('&', tag);

        Group group = publisher.publish(name, tag);

        if (group == null) {
            sender.send("society.already-exists", name, tag);
            return;
        }

        if (!verificationRequired) {
            Society society = group.get(Society.class);
            society.setVerified(true);
        }


        group.addMember(sender);
        sender.addRank(defaultRank);
        sender.addRank(superRank);

        sender.send("society.created", name, tag);
    }
}
