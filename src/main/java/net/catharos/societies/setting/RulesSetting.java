package net.catharos.societies.setting;

/**
 * Represents a RulesSetting
 */
public class RulesSetting extends BooleanSetting {

    public static final int ID = 0xFFF;

    private final String rule;

    public RulesSetting(String rule, int id) {
        super(ID + id);
        this.rule = rule;
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
