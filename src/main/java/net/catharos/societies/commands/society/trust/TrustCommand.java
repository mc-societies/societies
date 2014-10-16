package net.catharos.societies.commands.society.trust;

import net.catharos.groups.Member;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.reflect.Sender;

/**
 * Represents a RelationListCommand
 */
@Command(identifier = "command.trust")
@Sender(Member.class)
public class TrustCommand implements Executor<Member> {

    @Option(name = "argument.target.member")
    Member target;

    @Override
    public void execute(CommandContext<Member> ctx, Member sender) {

    }
}
