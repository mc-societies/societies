package org.societies.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import org.societies.bridge.ChatColor;

import javax.inject.Provider;

/**
 * Represents a DefaultHelpExecutor
 */
class GroupHelpExecutor<S extends Sender> implements Executor<S> {

    private final Provider<Table> tableProvider;

    //todo define default options for group command e.g. page -> no need to declare it
    @Option(name = "argument.page")
    int page;

    @Inject
    public GroupHelpExecutor(Provider<Table> tableProvider) {
        this.tableProvider = tableProvider;
    }

    @Override
    public void execute(CommandContext<S> ctx, S sender) {
        GroupCommand<S> command = (GroupCommand<S>) ctx.getCommand();

        Table table = tableProvider.get();

        execute(command, sender, table);

        String help = table.render("Help", ctx.getPage());
        sender.send(help);
    }

    public void execute(GroupCommand<S> command, final S sender, final Table table) {
        command.iterate(new FormatCommandIterator<S>(ChatColor.AQUA + "/", ChatColor.GRAY + " - ") {

            @Override
            public void iterate(Command<S> command, String format) {
                if (!command.getSenderClass().isInstance(sender)) {
                    return;
                }

                table.addRow(format);
            }
        });
    }
}
