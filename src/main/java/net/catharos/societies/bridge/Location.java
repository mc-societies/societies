package net.catharos.societies.bridge;

import net.catharos.lib.core.math.Vector3d;

/**
 * Represents a Location
 */
public class Location extends Vector3d {

    private final World world;
    private final float pitch, yaw, roll;

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0, 0);
    }

    public Location(World world, double x, double y, double z, float pitch, float yaw, float roll) {
        super(x, y, z);
        this.world = world;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public World getWorld() {
        return world;
    }
}
