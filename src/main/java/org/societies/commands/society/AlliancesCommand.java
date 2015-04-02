package org.societies.commands.society;

import com.google.inject.Inject;
import order.format.table.RowFactory;
import order.format.table.Table;
import order.reflect.Command;
import order.reflect.Permission;
import org.societies.groups.Relation;
import org.societies.groups.group.GroupProvider;

import javax.inject.Provider;

/**
 * Represents a AlliancesCommand
 */
@Command(identifier = "command.alliances", async = true)
@Permission("societies.alliances")
public class AlliancesCommand extends AbstractRelationsCommand {

    @Inject
    public AlliancesCommand(RowFactory rowFactory, GroupProvider groupProvider, Provider<Table> tableProvider) {
        super(groupProvider, tableProvider, rowFactory);
    }

    @Override
    protected Relation.Type getType() {
        return Relation.Type.ALLIED;
    }
}
