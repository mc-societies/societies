package net.catharos.societies.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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
    HelpExecutor(@Named("clean") Provider<Table> tableProvider) {this.tableProvider = tableProvider;}

    private void displayHelp(CommandContext<S> ctx, S sender, GroupCommand<S> command) {
        for (Command<S> child : command.getChildren()) {
            displayHelp(ctx, sender, child);
        }
    }

    private void displayHelp(CommandContext<S> ctx, S sender, ExecutableCommand<S> command) {
        StringBuilder help = new StringBuilder();

        StringBuilder commandFormat = new StringBuilder();

        //todo create iterator
        for (Command<S> cmd = command; cmd != null; cmd = cmd.getParent()) {
            if (cmd.getIdentifier() == null || cmd.getIdentifier().isEmpty()) {
                continue;
            }
            commandFormat.insert(0, cmd.getIdentifier() + " ");
        }

        commandFormat.insert(0, '/');

        help.append(commandFormat.toString());
        help.append("[OPTIONS] [ARGUMENTS]\n");

        if (!command.getOptions().isEmpty()) {
            help.append("Options:\n");
            help.append(arguments(command, true).render("options", ctx.getPage()));
        }

        if (!command.getArguments().isEmpty()) {
            help.append("\nArguments:\n");
            help.append(arguments(command, false).render("arguments", ctx.getPage()));
        }

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
