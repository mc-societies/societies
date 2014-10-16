package net.catharos.societies.commands.society;

import com.google.inject.Inject;
import net.catharos.groups.GroupProvider;
import net.catharos.groups.Relation;
import net.catharos.lib.core.command.format.table.RowFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Entry;
import net.catharos.lib.core.command.reflect.Meta;
import net.catharos.societies.commands.RuleStep;

import javax.inject.Provider;

/**
 * Represents a AlliancesCommand
 */
@Command(identifier = "command.rivalries")
@Meta(@Entry(key = RuleStep.RULE, value = "rivalries"))
public class RivalriesCommand extends AbstractRelationsCommand {

    @Inject
    public RivalriesCommand(RowFactory rowFactory, GroupProvider groupProvider, Provider<Table> tableProvider) {
        super(groupProvider, tableProvider, rowFactory);
    }

    @Override
    protected Relation.Type getType() {
        return Relation.Type.ALLIED;
    }
}
