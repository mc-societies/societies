package org.societies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import order.CommandContext;
import order.ParsingException;
import order.parser.ArgumentParser;
import order.sender.Sender;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.societies.api.math.Location;
import org.societies.groups.member.Member;

/**
 * Represents a LocationParser
 */
public class LocationParser implements ArgumentParser<Location> {

    private final Server worldResolver;
    private final World defaultWorld;

    @Inject
    public LocationParser(Server worldResolver, @Named("default-world") World defaultWorld) {
        this.worldResolver = worldResolver;
        this.defaultWorld = defaultWorld;
    }

    @Override
    public Location parse(String input, CommandContext<?> ctx) throws ParsingException {
        String[] parts = input.split(",");

        if (parts.length < 3) {
            throw new ParsingException("Invalid location!", ctx);
        }

        String worldName = null;
        int start = 0;

        if (parts.length == 4) {
            worldName = parts[0];
            start = 1;
        }

        double[] coordinates = new double[3];

        for (int i = start, splitLength = parts.length; i < splitLength; i++) {
            String part = parts[i].trim();

            double coordinate;

            try {
                coordinate = Double.parseDouble(part);
            } catch (NumberFormatException ignored) {
                throw new ParsingException("Invalid location!", ctx);
            }

            coordinates[i] = coordinate;
        }

        World world = null;

        if (worldName != null) {
            world = worldResolver.getWorld(worldName);
        }

        if (world == null) {
            Sender sender = ctx.getSender();

            if (sender instanceof Member) {
                Member member = ((Member) sender);

                Player player = member.get(Player.class);

                if (player.isOnline()) {
                    world = player.getWorld();
                }
            } else {
                world = this.defaultWorld;
            }
        }

        return new Location(world, coordinates[0], coordinates[1], coordinates[2]);
    }
}
