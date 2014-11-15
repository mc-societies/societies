package net.catharos.societies.setting;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.setting.Setting;
import net.catharos.groups.setting.SettingException;
import net.catharos.groups.setting.subject.Subject;
import net.catharos.groups.setting.target.Target;
import net.catharos.bridge.Location;
import net.catharos.bridge.World;
import net.catharos.bridge.WorldResolver;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Represents a LocationSetting
 */
class LocationSetting extends Setting<Location> {
    public static final int ID = 0x0;

    private final WorldResolver worldResolver;
    private final World defaultWorld;

    @Inject
    public LocationSetting(WorldResolver worldResolver, @Named("default-world") World defaultWorld) {
        super(ID);
        this.worldResolver = worldResolver;
        this.defaultWorld = defaultWorld;
    }

    @Override
    public Location convert(Subject subject, Target target, byte[] value) throws SettingException {
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(value));

        try {
            World world = worldResolver.getWorld(is.readUTF());
            return new Location(world == null ? defaultWorld : world,
                    is.readDouble(), is.readDouble(), is.readDouble(), is.readFloat(), is.readFloat(), is.readFloat());
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
            os.writeUTF(value.getWorld().getName());
            os.writeDouble(value.getX());
            os.writeDouble(value.getY());
            os.writeDouble(value.getZ());

            os.writeFloat(value.getYaw());
            os.writeFloat(value.getPitch());
            os.writeFloat(value.getRoll());
        } catch (IOException e) {
            throw new SettingException(e);
        }

        return out.toByteArray();
    }
}
