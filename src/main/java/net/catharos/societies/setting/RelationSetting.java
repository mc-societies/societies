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
    public static final int ID = 1;

    public RelationSetting() {
        super(ID);
    }

    @Override
    public Relation convert(Subject subject, Target target, byte[] value) {
        return new DefaultRelation(subject.getUUID(), target.getUUID());
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Relation value) {
        return new byte[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;


        RelationSetting that = (RelationSetting) o;

        return getID() == that.getID();

    }

    @Override
    public int hashCode() {
        return getID();
    }

}
