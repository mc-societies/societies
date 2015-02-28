package org.societies.sieging.commands.parser;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ParsingException;
import net.catharos.lib.core.command.parser.ArgumentParser;
import org.jetbrains.annotations.Nullable;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityProvider;

/**
 * Represents a CityParser
 */
public class CityParser implements ArgumentParser<City> {

    private CityProvider cityProvider;

    @Inject
    public CityParser(CityProvider cityProvider) {
        this.cityProvider = cityProvider;
    }

    @Nullable
    @Override
    public City parse(String input, CommandContext<?> ctx) throws ParsingException {
        return cityProvider.getCity(input).orNull(); //fixme Nullable
    }
}
