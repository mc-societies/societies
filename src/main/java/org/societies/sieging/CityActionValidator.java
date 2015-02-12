package org.societies.sieging;

import org.societies.api.sieging.AbstractActionValidator;
import org.societies.api.sieging.Besieger;
import org.societies.api.sieging.City;
import org.societies.api.sieging.CityProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Represents a CityActionValidator
 */
public class CityActionValidator extends AbstractActionValidator {

    @Inject
    public CityActionValidator(CityProvider cityProvider) {
        super(cityProvider);
    }

    @Override
    public boolean can(Action action, @Nullable Besieger besieger, @Nullable City city) {

        return false;
    }
}
