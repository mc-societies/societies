package org.societies.commands;

import com.google.inject.Inject;
import order.CommandContext;
import order.ExecuteException;
import order.Executor;
import order.reflect.Argument;
import order.reflect.Command;
import order.reflect.Permission;
import order.sender.Sender;
import order.sender.SenderWriter;

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
