package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.Material;

/**
 * Represents a BukkitMaterial
 */
public class BukkitMaterial implements Material {

    private final org.bukkit.Material material;

    public BukkitMaterial(org.bukkit.Material material) {this.material = material;}

    @Override
    public int getID() {
        return material.getId();
    }

    @Override
    public String getName() {
        return material.toString();
    }

    @Override
    public String getInternalName() {
        return material.toString();
    }

    @Override
    public boolean isFood() {
        return false;
    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public boolean isBoot() {
        return false;
    }

    @Override
    public boolean isHelmet() {
        return false;
    }

    @Override
    public boolean isChestplate() {
        return false;
    }

    @Override
    public boolean isLeggings() {
        return false;
    }

    @Override
    public boolean isWeapon() {
        return false;
    }
}
