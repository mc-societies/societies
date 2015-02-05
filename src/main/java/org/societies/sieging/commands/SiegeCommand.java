package org.societies.sieging.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.societies.api.sieging.City;
import org.societies.commands.RuleStep;
import org.societies.groups.member.Member;

/**
 * Represents a SiegeCommand
 */
@Command(identifier = "command.siege")
@Children({SiegeCommand.StartCommand.class,
        SiegeCommand.EndCommand.class,
        SiegeCommand.ListCommand.class})
public class SiegeCommand {

    @Command(identifier = "command.siege.start")
    @Permission("societies.siege.start")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public static class StartCommand implements Executor<Member> {

        @Argument
        City target;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            //location
        }
    }

    @Command(identifier = "command.siege.end")
    @Permission("societies.siege.end")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public static class EndCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.siege.list")
    @Permission("societies.siege.list")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    public static class ListCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }


}
