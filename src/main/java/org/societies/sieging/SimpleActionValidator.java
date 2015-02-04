package org.societies.sieging;

import org.societies.api.sieging.ActionValidator;
import org.societies.api.sieging.CityProvider;
import org.societies.bridge.Location;

/**
 * Represents a SimpleActionValidator
 */
public class SimpleActionValidator implements ActionValidator {

    private final CityProvider cityProvider;

    public SimpleActionValidator(CityProvider cityProvider) {
        this.cityProvider = cityProvider;
    }

    @Override
    public boolean canInteract(Location location) {
        return cityProvider.getCity(location).getOwner() != null;
    }

    @Override
    public boolean canDestroy(Location location) {
        return cityProvider.getCity(location).getOwner() != null;
    }

    @Override
    public boolean canBuild(Location location) {
        return cityProvider.getCity(location).getOwner() != null;
    }
}
