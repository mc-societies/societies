package org.societies.group;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import org.shank.AbstractModule;
import org.societies.api.setting.RulesSetting;
import org.societies.groups.setting.Setting;

/**
 * Represents a RuleModule
 */
public class RuleModule extends AbstractModule {
    @Override
    protected void configure() {
        addRule("*");
        addRule("invite");
        addRule("join");
        addRule("leave");
        addRule("vitals");
        addRule("roster");
        addRule("kick");
        addRule("coords");
        addRule("trust");
        addRule("untrust");
        addRule("tag");

        addRule("home.teleport");
        addRule("home.regroup");
        addRule("home.set");

        addRule("rank.assign");
        addRule("rank.create");
        addRule("rank.list");
        addRule("rank.remove");

        addRule("rank.rules.assign");
        addRule("rank.rules.list");
        addRule("rank.rules.remove");

        addRule("allies.list");
        addRule("allies.add");
        addRule("allies.remove");

        addRule("rivals.list");
        addRule("rivals.add");
        addRule("rivals.remove");

        addRule("vote.join");
        addRule("vote.allies");
        addRule("vote.rivals");

        addRule("leader");
    }


    private void addRule(String rule) {
        rules().addBinding(rule).toInstance(new RulesSetting(rule, rule.replace('.', '-')));
    }

    public MapBinder<String, Setting<Boolean>> rules() {
        return MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {
        }, new TypeLiteral<Setting<Boolean>>() {
        });
    }
}
