package net.catharos.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;

/**
 * Represents a ClanCommand
 */
@Command(identifier = "society", description = "A default description!")
@Children(children = {
        CreateCommand.class,
//        AbandonCommand.class,
        ProfileCommand.class,
        ListCommand.class,
//        InviteCommand.class,

//        RankCommand.class,
//        RelationCommand.class
})
public class SocietyCommand {
}
