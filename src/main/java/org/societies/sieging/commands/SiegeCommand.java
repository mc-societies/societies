package org.societies.sieging.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import org.societies.api.sieging.City;
import org.societies.commands.RuleStep;
import org.societies.groups.member.Member;

/**
 * Represents a SiegeCommand
 */
public class SiegeCommand {

    @Command(identifier = "command.sieging.start")
    @Permission("societies.sieging.start")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public class StartCommand implements Executor<Member> {

        @Argument
        City target;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            //location
        }
    }

    @Command(identifier = "command.sieging.end")
    @Permission("societies.sieging.end")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public class EndCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.sieging.list")
    @Permission("societies.sieging.list")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public class ListCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }


}
