package net.catharos.societies.commands.society.trust;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.trust")
public class TrustCommand implements Executor<Member> {

    @Option(name = "argument.member.target")
    Member target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {
        target.setState(0);
    }
}
