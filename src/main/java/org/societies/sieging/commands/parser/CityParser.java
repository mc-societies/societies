package org.societies.sieging.commands.parser;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import order.CommandContext;
import order.ParsingException;
import order.parser.ArgumentParser;
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
        Optional<City> city = cityProvider.getCity(input);
        if (!city.isPresent()) {
            throw new ParsingException("target-city.not-found", ctx);
        }

        return city.get();
    }
}
