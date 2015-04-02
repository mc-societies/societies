package org.societies;

import com.google.inject.Inject;
import order.CommandContext;
import order.ParsingException;
import order.parser.ArgumentParser;
import order.sender.Sender;
import org.societies.bridge.Location;
import org.societies.bridge.Player;
import org.societies.bridge.World;
import org.societies.bridge.WorldResolver;
import org.societies.groups.member.Member;

/**
 * Represents a LocationParser
 */
public class LocationParser implements ArgumentParser<Location> {

    private final WorldResolver worldResolver;

    @Inject
    public LocationParser(WorldResolver worldResolver) {
        this.worldResolver = worldResolver;
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

                if (player.isAvailable()) {
                    world = player.getWorld();
                }
            } else {
                world = worldResolver.getDefaultWorld();
            }
        }

        return new Location(world, coordinates[0], coordinates[1], coordinates[2]);
    }
}
