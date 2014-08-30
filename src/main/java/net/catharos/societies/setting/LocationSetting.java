package net.catharos.societies.setting;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.catharos.groups.setting.Setting;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Represents a LocationSetting
 */
class LocationSetting extends Setting<Location> {
    public static final int ID = 0;

    private final World world;

    @Inject
    public LocationSetting(@Named("default-world") World world) {
        super(ID);
        this.world = world;
    }

    @Override
    public Location convert(byte[] value) {
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(value));

        try {
            return new Location(world, is.readInt(), is.readInt(), is.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return world.getSpawnLocation();
    }

    @Override
    public byte[] convert(@Nullable Location value) {
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
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
