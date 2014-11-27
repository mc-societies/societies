package net.catharos.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.societies.commands.society.balance.BalanceCommand;
import net.catharos.societies.commands.society.balance.DepositCommand;
import net.catharos.societies.commands.society.balance.WithdrawCommand;
import net.catharos.societies.commands.society.home.HomeCommand;
import net.catharos.societies.commands.society.promote.DemoteCommand;
import net.catharos.societies.commands.society.promote.PromoteCommand;
import net.catharos.societies.commands.society.rank.RankCommand;
import net.catharos.societies.commands.society.relation.AlliesCommand;
import net.catharos.societies.commands.society.relation.RivalsCommand;
import net.catharos.societies.commands.society.trust.DistrustCommand;
import net.catharos.societies.commands.society.trust.TrustCommand;
import net.catharos.societies.commands.society.verify.DisproveCommand;
import net.catharos.societies.commands.society.verify.VerifyCommand;
import net.catharos.societies.commands.society.vote.AbstainCommand;
import net.catharos.societies.commands.society.vote.AcceptCommand;
import net.catharos.societies.commands.society.vote.CancelCommand;
import net.catharos.societies.commands.society.vote.DenyCommand;

/**
 * Represents a SocietyCommand
 */
@Command(identifier = "societies")
@Children({
        CreateCommand.class,
        ListCommand.class,

        ProfileCommand.class,
        LookupCommand.class,

        JoinCommand.class,
        InviteCommand.class,

        CoordsCommand.class,
        VitalsCommand.class,
        RosterCommand.class,

        TrustCommand.class,
        DistrustCommand.class,
        PromoteCommand.class,
        DemoteCommand.class,

        AlliancesCommand.class,
        RivalriesCommand.class,

        FFCommand.class,
        GroupFFCommand.class,
        TagCommand.class,
        KickCommand.class,
        LeaveCommand.class,

        AlliesCommand.class,
        RivalsCommand.class,

        HomeCommand.class,
        RankCommand.class,

        BalanceCommand.class,
        DepositCommand.class,
        WithdrawCommand.class,

        AcceptCommand.class,
        DenyCommand.class,
        AbstainCommand.class,
        CancelCommand.class,

        VerifyCommand.class,
        DisproveCommand.class,

        ReloadCommand.class,
        BackupCommand.class
})
public class SocietyCommand {
}
