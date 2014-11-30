package org.societies.setting;

import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.Target;

/**
 * Represents a VerifySetting
 */
public class DoubleSetting extends Setting<Double> {

    public DoubleSetting(int id) {
        super(id);
    }

    @Override
    public Double convert(Subject subject, Target target, byte[] value) {
        return java.nio.ByteBuffer.wrap(value).getDouble();
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Double value) {
        if (value == null) {
            return new byte[0];
        }

        byte[] bytes = new byte[8];
        return java.nio.ByteBuffer.wrap(bytes).putDouble(value).array();
    }

    @Override
    public Double convertFromString(Subject subject, Target target, String value) throws SettingException {
        return Double.parseDouble(value);
    }

    @Override
    public String convertToString(Subject subject, Target target, @Nullable Double value) throws SettingException {
        if (value == null) {
            return Strings.EMPTY;
        }

        return value.toString();
    }
}
