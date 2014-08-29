package net.catharos.societies.bukkit;

import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.parser.ArgumentParser;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a LocationParser
 */
public class LocationParser implements ArgumentParser<Location> {


    @NotNull
    @Override
    public Location parse(String input, CommandContext<?> ctx) throws ParsingException {
        String[] split = input.split(",");

        int[] coordinates = new int[3];

        for (int i = 0, splitLength = split.length; i < splitLength; i++) {
            String s = split[i];
            String trim = s.trim();

            int coord = Integer.parseInt(trim);
            coordinates[i] = coord;
        }

        return new Location(null, coordinates[0], coordinates[1], coordinates[2]);
    }
}
