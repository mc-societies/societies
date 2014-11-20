package net.catharos.societies.setting;

import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

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
