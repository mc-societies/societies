package net.catharos.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;

/**
 * Represents a ClanCommand
 */
@Command(identifier = "society", description = "A default description!")
@Children(children = {
        CreateCommand.class,
//        AbandonCommand.class,
        ProfileCommand.class,
        ListCommand.class,
        InviteCommand.class,

        AcceptCommand.class,
        DenyCommand.class,
        AbstainCommand.class

//        RankCommand.class,
//        RelationCommand.class
})
public class SocietyCommand {
}
