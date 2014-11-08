package net.catharos.societies.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.format.table.Table;
import net.catharos.lib.core.command.reflect.Option;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.societies.bridge.ChatColor;

import javax.inject.Provider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a DefaultHelpExecutor
 */
class GroupHelpExecutor<S extends Sender> implements Executor<S> {

    private final Provider<Table> tableProvider;

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

        LinkedList<Command> pre = new LinkedList<Command>();
        pre.push(command);
        execute(command, sender, pre, table);

        String help = table.render("Help", page);
        sender.send(help);
    }

    public void execute(GroupCommand<S> command, S sender, LinkedList<Command> pre, Table table) {

        if (!command.getSenderClass().isInstance(sender)) {
            return;
        }

        for (Command<S> cmd : command.getChildren()) {

            if (!cmd.getSenderClass().isInstance(sender)) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            if (cmd instanceof ExecutableCommand) {
                ExecutableCommand executableCommand = (ExecutableCommand) cmd;

                append(builder, executableCommand, pre);
                appendDesc(builder, cmd);

                table.addRow(builder);
            } else {
                append(builder, cmd, pre);
                appendDesc(builder, cmd);

                table.addRow(builder);

                pre.push(cmd);
                execute(((GroupCommand<S>) cmd), sender, pre, table);
                pre.pop();
            }
        }
    }

    private StringBuilder append(StringBuilder builder, Command<?> command, LinkedList<Command> pre) {
        builder.append(ChatColor.AQUA).append("  /");

        for (Iterator<Command> it = pre.descendingIterator(); it.hasNext(); ) {
            builder.append(it.next().getIdentifier()).append(' ');
        }

        builder.append(command.getIdentifier());
        return builder;
    }

    private StringBuilder appendDesc(StringBuilder builder, Command<?> command) {
        builder.append(ChatColor.WHITE).append(" - ").append(command.getDescription());
        return builder;
    }

    private StringBuilder append(StringBuilder builder, ExecutableCommand<?> command, LinkedList<Command> pre) {
        append(builder, (Command<?>) command, pre);

        List<Argument> arguments = command.getArguments();

        for (Argument argument : arguments) {
            builder.append(' ').append(argument.getName());
        }

        for (Argument option : command.getOptions().values()) {
            builder.append(" [--").append(option.getName()).append("]");
        }

        return builder;
    }
}
