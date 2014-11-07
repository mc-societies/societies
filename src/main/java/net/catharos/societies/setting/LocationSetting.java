package net.catharos.societies.setting;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import org.bukkit.Location;
import org.bukkit.World;
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
            os.writeInt(value.getBlockX());
            os.writeInt(value.getBlockY());
            os.writeInt(value.getBlockZ());
        } catch (IOException e) {
            throw new SettingException(e);
        }

        return out.toByteArray();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationSetting that = (LocationSetting) o;

        return getID() == that.getID();

    }

    @Override
    public int hashCode() {
        return getID();
    }
}
