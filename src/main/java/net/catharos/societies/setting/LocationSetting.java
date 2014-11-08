package net.catharos.societies.setting;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.societies.bridge.Location;
import net.catharos.societies.bridge.World;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Represents a LocationSetting
 */
class LocationSetting extends Setting<Location> {
    public static final int ID = 0x0;

    private final World world;

    @Inject
    public LocationSetting(@Named("default-world") World world) {
        super(ID);
        this.world = world;
    }

    @Override
    public Location convert(Subject subject, Target target, byte[] value) throws SettingException {
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(value));

        try {
            return new Location(world, is.readInt(), is.readInt(), is.readInt());
        } catch (IOException e) {
            throw new SettingException(e);
        }
    }

    @Override
    public byte[] convert(Subject subject, Target target, @Nullable Location value) throws SettingException {
        if (value == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(out);

        try {
            os.writeInt(value.getRoundedX());
            os.writeInt(value.getRoundedY());
            os.writeInt(value.getRoundedZ());
        } catch (IOException e) {
            throw new SettingException(e);
        }

        return out.toByteArray();
    }
}
