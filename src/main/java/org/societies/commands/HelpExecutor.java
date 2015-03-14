package org.societies.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.ChatColor;

import javax.inject.Provider;
import java.util.LinkedList;

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
        final StringBuilder help = new StringBuilder();

        new FormatCommandIterator<S>("/", " - ", " [?]") {
            @Override
            public void iterate(Command<S> command, String format) {
                help.append(ChatColor.GRAY).append(format);
            }
        }.iterate(ctx.getCommand(), new LinkedList<Command<S>>());

        help.append('\n');

        if (!command.getArguments().isEmpty()) {
            help.append("\nArguments:\n");
            help.append(arguments(command).render("arguments", ctx.getPage()));
        }

        if (!command.getOptions().isEmpty()) {
            help.append("Options:\n");
            help.append(options(command).render("options", ctx.getPage()));
        }

        sender.send(help);
    }

    private Table arguments(ExecutableCommand<S> command) {
        Table table = tableProvider.get();

        for (Argument argument : command.getArguments()) {
            table.addRow(ChatColor.GRAY + argument.getName(), ChatColor.DARK_GRAY + argument.getDescription());
        }

        return table;
    }

    private Table options(ExecutableCommand<S> command) {
        Table table = tableProvider.get();

        for (Argument argument : command.getOptions().values()) {
            table.addRow(ChatColor.GRAY + argument.getName(), ChatColor.DARK_GRAY + argument.getDescription());
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
