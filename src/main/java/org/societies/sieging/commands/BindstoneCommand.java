package org.societies.sieging.commands;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import org.societies.api.sieging.City;
import org.societies.commands.RuleStep;
import org.societies.groups.member.Member;

/**
 * Represents a BindstoneCommand
 */
@Command(identifier = "command.bindstone")
public class BindstoneCommand {

    @Command(identifier = "command.bindstone.create")
    @Permission("societies.bindstones.create")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public class CreateCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
             //location
        }
    }

    @Command(identifier = "command.bindstone.remove")
    @Permission("societies.bindstones.remove")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public class RemoveCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.bindstone.land.move")
    @Permission("societies.bindstones.land.move")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public class MoveLand implements Executor<Member> {

        @Argument
        String name;

        @Argument
        City from;

        @Argument
        City to;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.bindstone.list")
    @Permission("societies.bindstones.list")
    public class ListCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }
}
