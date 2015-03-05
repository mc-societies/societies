package org.societies.script;

import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.shank.service.AbstractServiceModule;
import org.societies.groups.group.GroupProvider;
import org.societies.groups.member.MemberProvider;

import javax.script.*;
import java.util.Map;

/**
 * Represents a ScriptModule
 */
public class ScriptModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        bindService().to(JavaScriptCompiler.class);
    }

    @Provides
    @Singleton
    public ScriptEngine provideEngine(Injector injector, MemberProvider members, GroupProvider groups) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.put("ctx", injector);
        engine.put("injector", injector);

        engine.put("members", members);
        engine.put("groups", groups);
        return engine;
    }

    @Provides
    public ScriptContext provideContext(ScriptEngine engine) {
        SimpleScriptContext context = new SimpleScriptContext();
        context.setBindings(from(engine, engine.getBindings(ScriptContext.ENGINE_SCOPE)), ScriptContext.ENGINE_SCOPE);
        return context;
    }

    private static Bindings from(ScriptEngine engine, Bindings parent) {
        Bindings bindings = engine.createBindings();

        for (Map.Entry<String, Object> stringObjectEntry : parent.entrySet()) {
            bindings.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        }

        return bindings;
    }
}
