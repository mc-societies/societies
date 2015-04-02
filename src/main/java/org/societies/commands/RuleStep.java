package org.societies.commands;

import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.sender.Sender;
import org.societies.groups.member.Member;

/**
 * Represents a RuleStep
 */
public class RuleStep implements Executor<Sender> {

    public static final String RULE = "rule";

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        if (!(sender instanceof Member)) {
            return;
        }

        Member member = ((Member) sender);

        String rule = ctx.getCommand().get(RULE);

        if (rule == null) {
            return;
        }

        if (!member.hasGroup()) {
            sender.send("society.not-found");
            ctx.cancel();
            return;
        }

        if (!member.hasRule("*") && !member.hasRule(rule)) {
            sender.send("no-rule");
            ctx.cancel();
        }
    }
}
