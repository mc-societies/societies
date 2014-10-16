package net.catharos.societies.commands;

import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;

/**
 * Represents a RuleStep
 */
public class RuleStep implements Executor<Member> {

    public static final String RULE = "rule";

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
        for (Rank rank : sender.getRanks()) {

        }
        if (!sender.hasPermission(ctx.getCommand())) {
            ctx.cancel();
            sender.send("no.permission");
        }
    }
}
