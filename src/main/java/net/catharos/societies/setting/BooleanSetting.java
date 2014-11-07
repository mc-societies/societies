package net.catharos.societies.setting;

import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a VerifySetting
 */
public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(int id) {
        super(id);
    }

    @Override
    public Boolean convert(Subject subject, Target target, byte[] value) {
        return !(value == null || value.length == 0);
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Boolean value) {
        if (value == null || !value) {
            return null;
        }

        return new byte[]{1};
    }
}
