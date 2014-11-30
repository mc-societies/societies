package org.societies.setting;

import org.jetbrains.annotations.Nullable;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.Target;

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

    @Override
    public Boolean convertFromString(Subject subject, Target target, String value) throws SettingException {
        return Boolean.parseBoolean(value);
    }

    @Override
    public String convertToString(Subject subject, Target target, @Nullable Boolean value) throws SettingException {
        if (value == null) {
            return Boolean.FALSE.toString();
        }

        return value.toString();
    }
}
