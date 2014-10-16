package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;

import javax.inject.Provider;

/**
 * Represents a AlliancesCommand
 */
@Command(identifier = "command.alliances")
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