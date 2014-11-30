package org.societies.setting;

import org.jetbrains.annotations.Nullable;
import org.societies.groups.DefaultRelation;
import org.societies.groups.Relation;
import org.societies.groups.setting.Setting;
import org.societies.groups.setting.SettingException;
import org.societies.groups.setting.subject.Subject;
import org.societies.groups.setting.target.Target;

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
