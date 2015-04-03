package org.societies.sieging;

import org.societies.api.sieging.*;
import org.societies.groups.Relation;
import org.societies.groups.group.Group;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Set;

/**
 * Represents a CityActionValidator
 */
public class ComplexActionValidator extends AbstractActionValidator {


    private final SiegeController siegeController;

    @Inject
    public ComplexActionValidator(CityProvider cityProvider, SiegeController siegeController) {
        super(cityProvider);
        this.siegeController = siegeController;
    }

    @Override
    public boolean can(int action, @Nullable Besieger besieger, @Nullable City city) {
        if (besieger == null || city == null) {
            return true;
        }

        Group group = besieger.getGroup();

        Besieger owner = city.getOwner();
        Group ownerGroup = owner.getGroup();

        if (ownerGroup.equals(group)) {
            return true;
        }

        Relation relation = ownerGroup.getRelation(group);

        if (relation.getType() == Relation.Type.ALLIED) {
            return true;
        }

        Set<Siege> sieges = siegeController.getSiegesByLocation(city);

        for (Siege siege : sieges) {
            if (siege.isStarted() && siege.getBesieger().equals(besieger)) {
                return true;
            }
        }

        return false;
    }
}
