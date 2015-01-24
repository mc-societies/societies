package org.societies.group;

import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import org.shank.AbstractModule;
import org.societies.groups.setting.Setting;
import org.societies.api.setting.RulesSetting;

/**
 * Represents a RuleModule
 */
public class RuleModule extends AbstractModule {
    @Override
    protected void configure() {
        addRule("*", 0x0);
        addRule("invite", 0x1);
        addRule("join", 0x2);
        addRule("leave", 0x3);
        addRule("vitals", 0x4);
        addRule("roster", 0x5);
        addRule("kick", 0x6);
        addRule("coords", 0x7);
        addRule("trust", 0x8);
        addRule("untrust", 0x9);
        addRule("tag", 0xA);

        addRule("home.teleport", 0x20);
        addRule("home.regroup", 0x21);
        addRule("home.set", 0x22);

        addRule("rank.assign", 0x30);
        addRule("rank.create", 0x31);
        addRule("rank.list", 0x32);
        addRule("rank.remove", 0x33);

        addRule("rank.rules.assign", 0x40);
        addRule("rank.rules.list", 0x41);
        addRule("rank.rules.remove", 0x42);

        addRule("allies.list", 0x50);
        addRule("allies.add", 0x51);
        addRule("allies.remove", 0x52);

        addRule("rivals.list", 0x60);
        addRule("rivals.add", 0x61);
        addRule("rivals.remove", 0x62);

        addRule("vote.join", 0x70);
        addRule("vote.allies", 0x71);
        addRule("vote.rivals", 0x72);

        addRule("leader", 0x80);
    }


    private void addRule(String rule, int id) {
        rules().addBinding(rule).toInstance(new RulesSetting(rule, id));
    }

    public MapBinder<String, Setting<Boolean>> rules() {
        return MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {}, new TypeLiteral<Setting<Boolean>>() {});
    }
}
