package net.catharos.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.ThreadTestCommand;
import net.catharos.societies.commands.society.home.HomeCommand;
import net.catharos.societies.commands.society.rank.RankCommand;
import net.catharos.societies.commands.society.relation.AlliesCommand;
import net.catharos.societies.commands.society.relation.RivalsCommand;
import net.catharos.societies.commands.society.verify.DisproveCommand;
import net.catharos.societies.commands.society.verify.VerifyCommand;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.CancelCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;

/**
 * Represents a SocietyCommand
 */
@Command(identifier = "society")
@Children({
        CreateCommand.class,
        TagCommand.class,
//        RenameCommand.class,
        ProfileCommand.class,
        LookupCommand.class,
        CoordsCommand.class,
        VitalsCommand.class,
        RosterCommand.class,
        ListCommand.class,

//        TrustCommand.class,
//        UnTrustCommand.class,

        JoinCommand.class,
        InviteCommand.class,
        KickCommand.class,

        AlliancesCommand.class,
        RivalriesCommand.class,

        JoinCommand.class,
//        FastJoinCommand.class,
        LeaveCommand.class,

        AcceptCommand.class,
        DenyCommand.class,
        AbstainCommand.class,
        CancelCommand.class,

        HomeCommand.class,

        RankCommand.class,

        AlliesCommand.class,
        RivalsCommand.class,

        FFCommand.class,
        GroupFFCommand.class,

        VerifyCommand.class,
        DisproveCommand.class,

        ReloadCommand.class,
        BackupCommand.class,
        ThreadTestCommand.class
})
public class SocietyCommand {
}
