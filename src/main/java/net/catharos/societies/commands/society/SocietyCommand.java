package net.catharos.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.ThreadTestCommand;
import net.catharos.societies.commands.society.home.HomeCommand;
import net.catharos.societies.commands.society.rank.RankCommand;
import net.catharos.societies.commands.society.relation.RelationCommand;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;

/**
 * Represents a SocietyCommand
 */
@Command(identifier = "society")
@Children({
        CreateCommand.class,
        RenameCommand.class,
        ProfileCommand.class,
        LookupCommand.class,
        RosterCommand.class,
        ListCommand.class,
        InviteCommand.class,
        KickCommand.class,

        JoinCommand.class,
        FastJoinCommand.class,
        LeaveCommand.class,

        AcceptCommand.class,
        DenyCommand.class,
        AbstainCommand.class,

        HomeCommand.class,

        RankCommand.class,
        RelationCommand.class,
        ThreadTestCommand.class
})
public class SocietyCommand {
}
