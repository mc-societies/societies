package net.catharos.societies.commands;

import com.google.inject.Inject;
import net.catharos.groups.GroupProvider;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.member.SocietyMember;

/**
 * Represents a ClanCommand
 */
@Command(identifier = "society", description = "A default description!")
@Children(children = SocietyCommand.CreateCommand.class)
public class SocietyCommand {


    @Command(identifier = "create", description = "A default description!")
    public static class CreateCommand implements Executor<SocietyMember> {



        @Argument(name = "name", description = "The name of the new society")
        protected String name;

        private final GroupProvider groupProvider;

        @Inject
        CreateCommand(GroupProvider groupProvider) {
            this.groupProvider = groupProvider;
        }

        @Override
        public void execute(CommandContext<SocietyMember> ctx, SocietyMember sender) {
            sender.send(name + " created!");
        }
    }
}
