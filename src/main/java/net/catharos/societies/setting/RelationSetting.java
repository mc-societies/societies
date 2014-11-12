package net.catharos.societies.setting;

import net.catharos.groups.DefaultRelation;
import net.catharos.groups.Relation;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.jetbrains.annotations.Nullable;

import static java.lang.Byte.parseByte;

/**
 * Represents a RelationSetting
 */
public class RelationSetting extends Setting<Relation> {
    public static final int ID = 0x1;

    public RelationSetting() {
        super(ID);
    }

    @Override
    public Relation convert(Subject subject, Target target, byte[] value) {
        byte type = value[0];
        return new DefaultRelation(subject.getUUID(), target.getUUID(), Relation.Type.getType(type));
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Relation value) {
        if (value == null) {
            return null;
        }

        return new byte[]{value.getType().getID()};
    }

    @Override
    public Relation convertFromString(Subject subject, Target target, String value) throws SettingException {
        try {
            return new DefaultRelation(subject.getUUID(), target.getUUID(), Relation.Type.getType(parseByte(value)));
        } catch (NumberFormatException e) {
            throw new SettingException(e);
        }
    }

    @Override
    public String convertToString(Subject subject, Target target, @Nullable Relation value) throws SettingException {
        if (value == null) {
            return null;
        }

        return String.valueOf(value.getType().getID());
    }
}
