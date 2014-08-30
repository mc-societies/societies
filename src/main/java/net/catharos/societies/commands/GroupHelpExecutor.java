package net.catharos.societies.commands;

import net.catharos.lib.core.command.*;
import net.catharos.lib.core.command.sender.Sender;
import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a DefaultHelpExecutor
 */
class GroupHelpExecutor<S extends Sender> implements Executor<S> {

    @Override
    public void execute(CommandContext<S> ctx, S sender) {
        GroupCommand<S> command = (GroupCommand<S>) ctx.getCommand();

        LinkedList<Command> pre = new LinkedList<Command>();
        pre.push(command);
        execute(command, sender, pre);
    }

    public void execute(GroupCommand<S> command, S sender, LinkedList<Command> pre) {
        for (Command<S> cmd : command.getChildren()) {
            StringBuilder builder = new StringBuilder();
            if (cmd instanceof ExecutableCommand) {
                ExecutableCommand executableCommand = (ExecutableCommand) cmd;

                append(builder, executableCommand, pre);
                appendDesc(builder, cmd);
            } else {
                append(builder, cmd, pre);
                appendDesc(builder, cmd);
                pre.push(cmd);
                execute(((GroupCommand<S>) cmd), sender, pre);
                pre.pop();
            }

            sender.send(builder.toString());
        }
    }

    private StringBuilder append(StringBuilder builder, Command<?> command, LinkedList<Command> pre) {
        builder.append(ChatColor.AQUA).append("  /");


        for (Command cmd : pre) {
            builder.append(cmd.getIdentifier()).append(' ');
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
