package org.societies.script;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;

import javax.script.ScriptEngine;

/**
 * Represents a JavaScriptCompiler
 */
public class JavaScriptCompiler extends AbstractService {

    private final ScriptEngine scriptEngine;
    private final Logger logger;

    @Inject
    public JavaScriptCompiler(ScriptEngine scriptEngine, Logger logger) {
        this.scriptEngine = scriptEngine;
        this.logger = logger;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        logger.info("Compiling JavaScript helpers...");
        scriptEngine.eval(
                "function get(name) {return injector.getInstance(Java.type('java.lang.Class').forName(name));}" +
                        "function getMember(name) {return members.getMember(name).orNull();}" +
                        "function getMemberByUUID(uuid) {return members.getMember(new java.util.UUID(uuid));}" +
                        "function getGroups(name) {return groups.getGroup(name).orNull();}" +
                        "function getGroupByUUID(uuid) {return groups.getGroup(new java.util.UUID(uuid)).orNull();}"
        );
    }
}
