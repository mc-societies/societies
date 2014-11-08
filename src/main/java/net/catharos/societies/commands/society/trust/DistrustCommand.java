package net.catharos.societies.commands.society.trust;

import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.rank.Rank;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.societies.commands.RuleStep;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.distrust")
@Permission("societies.distrust")
@Meta(@Entry(key = RuleStep.RULE, value = "distrust"))
@Sender(Member.class)
//fixme
public class DistrustCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    private final Rank defaultRank;
    private final Rank normalDefaultRank;

    @Inject
    public DistrustCommand(Rank defaultRank, Rank normalDefaultRank) {
        this.defaultRank = defaultRank;
        this.normalDefaultRank = normalDefaultRank;
    }

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        sender.removeRank(normalDefaultRank);
    }
}
