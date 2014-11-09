package net.catharos.societies.bukkit;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.parser.ArgumentParser;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.bridge.WorldResolver;
import net.catharos.societies.bridge.Location;
import net.catharos.societies.bridge.World;
import net.catharos.societies.member.SocietyMember;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a LocationParser
 */
public class LocationParser implements ArgumentParser<Location> {

    private final WorldResolver worldResolver;

    @Inject
    public LocationParser(WorldResolver worldResolver) {
        this.worldResolver = worldResolver;
    }

    @NotNull
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
            } catch (NumberFormatException e) {
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

            if (sender instanceof SocietyMember) {
                SocietyMember societyMember = ((SocietyMember) sender);

                if (societyMember.isAvailable()) {
                    world = societyMember.getLocation().getWorld();
                }
            } else {
                world = worldResolver.getDefaultWorld();
            }
        }

        return new Location(world, coordinates[0], coordinates[1], coordinates[2]);
    }
}
