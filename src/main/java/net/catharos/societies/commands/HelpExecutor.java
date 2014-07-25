package net.catharos.societies.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.sender.Sender;

import javax.inject.Provider;

/**
 * Represents a DefaultHelpExecutor
 */
class HelpExecutor<S extends Sender> implements Executor<S> {

    private final Provider<Table> tableProvider;

    @Inject
    HelpExecutor(Provider<Table> tableProvider) {this.tableProvider = tableProvider;}

    private void displayHelp(CommandContext<S> ctx, S sender, GroupCommand<S> command) {
        for (Command<S> child : command.getChildren()) {
            displayHelp(ctx, sender, child);
        }
    }

    private void displayHelp(CommandContext<S> ctx, S sender, ExecutableCommand<S> command) {
        StringBuilder help = new StringBuilder();

        help.append(command.getIdentifier());
        help.append(" [OPTIONS] [ARGUMENTS]\n");

        help.append("Options:\n");
        help.append(arguments(command, true).render(ctx.getPage()));

        help.append("\nArguments:\n");
        help.append(arguments(command, false).render(ctx.getPage()));

        sender.send(help);
    }

    private Table arguments(ExecutableCommand<S> command, boolean options) {
        Table table = tableProvider.get();

        for (Argument argument : command.getArguments()) {
            if ((options && argument.isOption()) || (!options && !argument.isOption())) {
                table.addRow(argument.getName(), argument.getDescription());
            }
        }

        return table;
    }

    @Override
    public void execute(CommandContext<S> ctx, S sender) {
        Command<S> command = ctx.getCommand();
        displayHelp(ctx, sender, command);
    }

    private void displayHelp(CommandContext<S> ctx, S sender, Command<S> command) {
        if (command instanceof GroupCommand) {
            displayHelp(ctx, sender, (GroupCommand<S>) command);
        } else {
            displayHelp(ctx, sender, (ExecutableCommand<S>) command);
        }
    }
}
