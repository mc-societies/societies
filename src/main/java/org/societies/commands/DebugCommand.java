package org.societies.commands;

import com.google.inject.Inject;
import net.catharos.lib.core.command.CommandContext;
import net.catharos.lib.core.command.ExecuteException;
import net.catharos.lib.core.command.Executor;
import net.catharos.lib.core.command.reflect.Argument;
import net.catharos.lib.core.command.reflect.Command;
import net.catharos.lib.core.command.reflect.Permission;
import net.catharos.lib.core.command.sender.Sender;
import net.catharos.lib.core.command.sender.SenderWriter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Represents a DebugCommand
 */
@Command(identifier = "command.debug")
@Permission("societies.debug")
public class DebugCommand implements Executor<Sender> {

    @Argument
    String debugCommand;

    private final ScriptEngine engine;
    private final ScriptContext context;

    @Inject
    public DebugCommand(ScriptEngine engine, ScriptContext context) {
        this.engine = engine;
        this.context = context;
    }

    @Override
    public void execute(CommandContext<Sender> ctx, Sender sender) throws ExecuteException {
        try {
            context.setWriter(new SenderWriter(sender));
            context.getBindings(ScriptContext.ENGINE_SCOPE).put("me", sender);

            Object result = engine.eval(debugCommand, context);

            sender.send("Result: " + result);
        } catch (ScriptException e) {
            sender.send(e.getMessage());
        }
    }
}
