package net.catharos.societies.setting;

import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a RulesSetting
 */
public class RulesSetting extends Setting<Boolean> {

    private final String rule;

    public RulesSetting(String rule, int id) {
        super(id);
        this.rule = rule;
    }

    @Override
    public Boolean convert(Subject subject, Target target, byte[] value) {
        return true;
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Boolean value) {
        if (value != null && value) {
            return new byte[0];
        } else {
            return null;
        }
    }

    public String getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return "RulesSetting{" +
                "rule='" + rule + '\'' +
                '}';
    }
}
