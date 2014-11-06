package net.catharos.societies.setting;

import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a VerifySetting
 */
public class VerifySetting extends Setting<Boolean> {

    public static final int ID = 0x3;

    public VerifySetting() {
        super(ID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;


        VerifySetting that = (VerifySetting) o;

        return getID() == that.getID();
    }

    @Override
    public int hashCode() {
        return getID();
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
