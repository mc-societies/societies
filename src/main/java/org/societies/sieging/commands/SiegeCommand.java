package org.societies.sieging.commands;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.SiegeController;
import org.societies.bridge.Location;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
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
    @Sender(Member.class)
    public static class StartCommand implements Executor<Member> {

        @Argument(name = "argument.target.city")
        City target;

        private final SiegeController siegeController;

        @Inject
        public StartCommand(SiegeController siegeController) {
            this.siegeController = siegeController;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Besieger besieger = group.get(Besieger.class);
            Player player = sender.get(Player.class);
            Location location = player.getLocation();

            siegeController.start(besieger, target, location);
            sender.send("siege.started", target.getName());
        }
    }

    @Command(identifier = "command.siege.end")
    @Permission("societies.siege.end")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    @Sender(Member.class)
    public static class EndCommand implements Executor<Member> {

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            //todo
        }
    }

    @Command(identifier = "command.siege.list")
    @Permission("societies.siege.list")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    @Sender(Member.class)
    public static class ListCommand implements Executor<Member> {


        private final SiegeController siegeController;
        private final Provider<Table> tableProvider;
        private final RowFactory rowFactory;

        @Option(name = "argument.page")
        int page;

        @Inject
        public ListCommand(SiegeController siegeController, Provider<Table> tableProvider, RowFactory rowFactory) {
            this.siegeController = siegeController;
            this.tableProvider = tableProvider;
            this.rowFactory = rowFactory;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Besieger besieger = group.get(Besieger.class);

            Table table = tableProvider.get();

            table.addForwardingRow(rowFactory.translated(true, "besieger", "city"));

            for (Siege siege : siegeController.getSieges(besieger)) {
                table.addRow(siege.getBesieger().getGroup().getName(), siege.getCity().getName());
            }

            for (City city : besieger.getCities()) {
                for (Siege siege : siegeController.getSieges(city)) {
                    table.addRow(siege.getBesieger().getGroup().getName(), siege.getCity().getName());
                }
            }

            sender.send(table.render(ctx.getName(), page));
        }
    }
}
