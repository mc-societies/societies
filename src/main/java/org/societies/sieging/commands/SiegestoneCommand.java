package org.societies.sieging.commands;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.format.table.RowFactory;
import order.format.table.Table;
import order.reflect.*;
import order.reflect.instance.Children;
import org.bukkit.entity.Player;
import org.societies.api.math.Location;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.Siege;
import org.societies.api.sieging.SiegeController;
import org.societies.commands.RuleStep;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import java.util.Set;

/**
 * Represents a SiegeCommand
 */
@Command(identifier = "command.siegestone")
@Children({SiegestoneCommand.CreateCommand.class,
        SiegestoneCommand.ListCommand.class})
@Sender(Member.class)
public class SiegestoneCommand {

    @Command(identifier = "command.siegestone.create")
    @Permission("societies.siegestone.create")
    @Meta(@Entry(key = RuleStep.RULE, value = "sieging"))
    @Sender(Member.class)
    public static class CreateCommand implements Executor<Member> {

        @Argument(name = "argument.target.city")
        City target;

        private final SiegeController siegeController;

        private final double minSiegeDistance;

        @Inject
        public CreateCommand(SiegeController siegeController, @Named("sieging.min-distance") double minSigeDistance) {
            this.siegeController = siegeController;
            this.minSiegeDistance = minSigeDistance;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Group targetGroup = target.getOwner().getGroup();

            if (group.getRelation(targetGroup).getType() != Relation.Type.RIVALED) {
                sender.send("siege.no-rivals", targetGroup.getName());
                return;
            }

            Besieger besieger = group.get(Besieger.class);

            if (siegeController.getSiegeByAttacker(besieger).isPresent()) {
                sender.send("siege.already-sieging");
                return;
            }

            if (besieger.getCities().contains(target)) {
                sender.send("siege.sieging-own-city");
                return;
            }

            Player player = sender.get(Player.class);
            Location location = new Location(player.getLocation()).floor();

            double distance = target.distance(location);
            if (distance < minSiegeDistance) { //todo max distance
                sender.send("siege.siegestone-too-close", distance, minSiegeDistance);
                return;
            }

            Siege siege = siegeController.start(besieger, target, location);

            boolean applied = siege.getWager().apply(besieger.getGroup());

            if (!applied) {
                //fixme
                sender.send("wager.not-applied");
                return;
            }

            sender.send("siege.started", group.getName(), target.getName());
            targetGroup.send("siege.started", group.getName(), target.getName());
        }
    }

    @Command(identifier = "command.siegestone.list")
    @Permission("societies.siegestone.list")
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

            Optional<Siege> initiatedSiege = siegeController.getSiegeByAttacker(besieger);

            Set<City> cities = besieger.getCities();

            if (cities.isEmpty() && !initiatedSiege.isPresent()) {
                sender.send("siege.no-sieges");
                return;
            }

            Table table = tableProvider.get();

            table.addForwardingRow(rowFactory.translated(true, "besieger", "city"));


            if (initiatedSiege.isPresent()) {
                table.addRow(initiatedSiege.get().getBesieger().getGroup().getName(), initiatedSiege.get().getCity().getName());
            }


            for (City city : cities) {
                for (Siege siege : siegeController.getSiegesByLocation(city)) {
                    table.addRow(siege.getBesieger().getGroup().getName(), siege.getCity().getName());
                }
            }

            sender.send(table.render(ctx.getName(), page));
        }
    }
}
