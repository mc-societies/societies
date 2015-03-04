package org.societies.script;

import org.shank.AbstractModule;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;

/**
 * Represents a NaughtyScriptModule
 */
public class NaughtyScriptModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ScriptEngine.class).to(NaughtyScriptEngine.class);
        bind(ScriptContext.class).to(SimpleScriptContext.class);
    }
}
