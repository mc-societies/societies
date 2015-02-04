package org.societies.sieging.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.*;
import net.catharos.lib.core.command.reflect.instance.Children;
import net.catharos.lib.core.uuid.UUIDGen;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityProvider;
import org.societies.api.sieging.CityPublisher;
import org.societies.bridge.Location;
import org.societies.bridge.Player;
import org.societies.commands.RuleStep;
import org.societies.groups.member.Member;
import org.societies.sieging.DefaultLand;

/**
 * Represents a BindstoneCommand
 */
@Command(identifier = "command.bindstone")
@Children({BindstoneCommand.CreateCommand.class,
        BindstoneCommand.RemoveCommand.class,
        BindstoneCommand.MoveLand.class,
        BindstoneCommand.ListCommand.class,
        BindstoneCommand.InfoCommand.class})
public class BindstoneCommand {

    @Command(identifier = "command.bindstone.create")
    @Permission("societies.bindstones.create")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public static class CreateCommand implements Executor<Member> {

        @Argument
        String name;

        private final CityPublisher cityPublisher;

        @Inject
        public CreateCommand(CityPublisher cityPublisher) {
            this.cityPublisher = cityPublisher;
        }

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Player player = sender.get(Player.class);

            Location location = player.getLocation();
            City city = cityPublisher.publish(name, location, sender.getGroup().get(Besieger.class));


            city.addLand(new DefaultLand(UUIDGen.generateType1UUID(), city));
            city.addLand(new DefaultLand(UUIDGen.generateType1UUID(), city));
        }
    }

    @Command(identifier = "command.bindstone.remove")
    @Permission("societies.bindstones.remove")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public static class RemoveCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.bindstone.land.move")
    @Permission("societies.bindstones.land.move")
    @Meta(@Entry(key = RuleStep.RULE, value = "bindstone"))
    public static class MoveLand implements Executor<Member> {

        @Argument
        String name;

        @Argument
        City from;

        @Argument
        City to;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.bindstone.list")
    @Permission("societies.bindstones.list")
    public static class ListCommand implements Executor<Member> {

        @Argument
        String name;

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {

        }
    }

    @Command(identifier = "command.bindstone.info")
    @Permission("societies.bindstones.info")
    public static class InfoCommand implements Executor<Member> {

        private final CityProvider cityProvider;

        @Inject
        public InfoCommand(CityProvider cityProvider) {this.cityProvider = cityProvider;}

        @Override
        public void execute(CommandContext<Member> ctx, Member sender) throws ExecuteException {
            Location location = sender.get(Player.class).getLocation();
            sender.send("" + cityProvider.getCity(location));
        }
    }
}
