package org.societies.commands.society;

import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.societies.commands.society.balance.BalanceCommand;
import org.societies.commands.society.balance.DepositCommand;
import org.societies.commands.society.balance.WithdrawCommand;
import org.societies.commands.society.home.HomeCommand;
import org.societies.commands.society.promote.DemoteCommand;
import org.societies.commands.society.promote.PromoteCommand;
import org.societies.commands.society.rank.RankCommand;
import org.societies.commands.society.relation.AlliesCommand;
import org.societies.commands.society.relation.RivalsCommand;
import org.societies.commands.society.trust.DistrustCommand;
import org.societies.commands.society.trust.TrustCommand;
import org.societies.commands.society.verify.DisproveCommand;
import org.societies.commands.society.verify.VerifyCommand;
import org.societies.commands.society.vote.AbstainCommand;
import org.societies.commands.society.vote.AcceptCommand;
import org.societies.commands.society.vote.CancelCommand;
import org.societies.commands.society.vote.DenyCommand;

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
