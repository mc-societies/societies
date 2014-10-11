package net.catharos.societies.commands.society.relation;

import com.google.inject.Inject;
import net.catharos.groups.Member;
import net.catharos.groups.Relation;
import net.catharos.groups.RelationFactory;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Sender;
import net.catharos.lib.core.command.reflect.instance.Children;

import javax.inject.Provider;

/**
 * Represents a RelationCommand
 */
@Command(identifier = "command.rivals")
@Children({
        RivalsCommand.AddCommand.class,
        RivalsCommand.RemoveCommand.class
})
@Sender(Member.class)
public class RivalsCommand extends ListCommand {

    public static final Relation.Type TYPE = Relation.Type.RIVALED;

    @Inject
    public RivalsCommand(Provider<Table> tableProvider) {
        super(tableProvider);
    }

    @Override
    protected Relation.Type getType() {
        return TYPE;
    }

    @Command(identifier = "command.rivals.remove")
    @Sender(Member.class)
    public static class RemoveCommand extends net.catharos.societies.commands.society.relation.RemoveCommand {
        @Override
        protected String getSuccessMessage() {
            return "rivals.removed";
        }

        @Override
        protected Relation.Type getType() {
            return TYPE;
        }
    }

    @Command(identifier = "command.rivals.add", async = true)
    @Sender(Member.class)
    public static class AddCommand extends net.catharos.societies.commands.society.relation.AddCommand {

        @Inject
        public AddCommand(RelationFactory factory) {
            super(factory);
        }

        @Override
        protected String getSuccessMessage() {
              return "rivals.added";
        }

        @Override
        protected Relation.Type getType() {
            return TYPE;
        }
    }

}
