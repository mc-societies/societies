package org.societies.sieging.commands;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import org.societies.api.sieging.*;
import org.societies.bridge.Location;
import org.societies.bridge.Material;
import org.societies.bridge.Materials;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.groups.group.Group;
import org.societies.groups.member.Member;

import java.util.UUID;

/**
 * Represents a BindstoneCommand
 */
@Command(identifier = "command.bindstone")
@Children({BindstoneCommand.CreateCommand.class,
        BindstoneCommand.RemoveCommand.class,
        BindstoneCommand.MoveLand.class,
        BindstoneCommand.ListCommand.class,
        BindstoneCommand.InfoCommand.class,
        BindstoneCommand.Visualize.class})
@Sender(Member.class)
public class BindstoneCommand {

    @Command(identifier = "command.bindstone.create")
    @Permission("societies.bindstones.create")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    @Sender(Member.class)
    public static class CreateCommand implements Executor<Member> {

        @Argument
        String name;

        private final CityProvider cityProvider;
        private final CityPublisher cityPublisher;

        private final double minDistance;

        @Inject
        public CreateCommand(CityProvider cityProvider, CityPublisher cityPublisher, @Named("city.min-distance") double minDistance) {
            this.cityProvider = cityProvider;
            this.cityPublisher = cityPublisher;
            this.minDistance = minDistance;
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
            Location location = player.getLocation().floor();

            Optional<City> city = cityProvider.getNearestCity(location);

            if (city.isPresent()) {
                double distance = city.get().distance(location);

                if (distance < minDistance) {
                    sender.send("city.too-close");
                    return;
                }
            }


            City published = cityPublisher.publish(name, location, besieger);

            for (int i = 0; i < 5; i++) {
                published.addLand(new SimpleLand(UUID.randomUUID(), published));
            }

            sender.send("city.created", name);
        }
    }

    @Command(identifier = "command.bindstone.remove")
    @Permission("societies.bindstones.remove")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    @Sender(Member.class)
    public static class RemoveCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Besieger besieger = group.get(Besieger.class);

            besieger.removeCity(name);

            sender.send("city.removed", name);
        }
    }

    @Command(identifier = "command.bindstone.land.move")
    @Permission("societies.bindstones.land.move")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    @Sender(Member.class)
    public static class MoveLand implements Executor<Member> {

        @Argument(name = "argument.target.city.from")
        City from;

        @Argument(name = "argument.target.city.to")
        City to;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Group group = sender.getGroup();

            if (group == null) {
                sender.send("society.not-found");
                return;
            }

            Land land = Iterables.getFirst(from.getLands(), null);

            if (land == null) {
                sender.send("city.lands.none");
                return;
            }

            from.removeLand(land.getUUID());
            to.addLand(land);

            sender.send("city.lands.moved", from.getName(), to.getName());
        }
    }

    @Command(identifier = "command.bindstone.visualize")
    @Permission("societies.bindstones.visualize")
    @Meta(@Entry(key = RuleStep.RULE, value = "visualize"))
    @Sender(Member.class)
    public static class Visualize implements Executor<Member> {

        private static final double STEPS = 6;
        private static final double DEGREES = 2 * Math.PI / STEPS;

        @Argument(name = "argument.target.city")
        City target;

        private final Materials materials;

        @Inject
        public Visualize(Materials materials) {this.materials = materials;}

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

            Player player = sender.get(Player.class);
            Location location = target.getLocation();

            double current = 0;

            for (int i = 0; i < STEPS; i++) {

                Location vector3d = new Location(location.getWorld(), location.getX() + Math.cos(current) * target.getRadius(), 0, location
                        .getZ() + Math.sin(current) * target.getRadius());

                Material material = materials.getMaterial(20);

                for (int j = 0; j < 255; j++) {
                    player.sendBlockChange(vector3d = vector3d.add(0,1,0).floor(), material, (byte) 0);
                }

                current += DEGREES;
            }
        }
    }

    @Command(identifier = "command.bindstone.list")
    @Permission("societies.bindstones.list")
    @Sender(Member.class)
    public static class ListCommand implements Executor<Member> {

        private final Provider<Table> tableProvider;
        private final RowFactory rowFactory;

        @Option(name = "argument.page")
        int page;

        @Inject
        public ListCommand(Provider<Table> tableProvider, RowFactory rowFactory) {
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

            table.addForwardingRow(rowFactory.translated(true, "city", "lands"));

            for (City city : besieger.getCities()) {
                table.addRow(city.getName(), Integer.toString(city.getLands().size()));
            }

            sender.send(table.render(ctx.getName(), page));
        }
    }

    @Command(identifier = "command.bindstone.info")
    @Permission("societies.bindstones.info")
    @Sender(Member.class)
    public static class InfoCommand implements Executor<Member> {

        private final CityProvider cityProvider;

        @Inject
        public InfoCommand(CityProvider cityProvider) {this.cityProvider = cityProvider;}

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Location location = sender.get(Player.class).getLocation().floor();
            Optional<City> city = cityProvider.getCity(location);

            if (!city.isPresent()) {
                sender.send("city.not-found");
                return;
            }

            sender.send("cities.list", city.get().getName());
        }
    }
}
