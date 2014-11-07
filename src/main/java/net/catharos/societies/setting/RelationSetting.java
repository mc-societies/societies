package net.catharos.societies.setting;

import net.catharos.groups.DefaultRelation;
import net.catharos.groups.Relation;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.jetbrains.annotations.Nullable;

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
}
