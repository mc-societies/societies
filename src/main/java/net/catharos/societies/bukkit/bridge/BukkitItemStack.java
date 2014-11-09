package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.ItemStack;
import net.catharos.societies.bridge.Material;
import net.catharos.societies.bridge.Materials;

/**
 * Represents a BukkitItemStack
 */
public class BukkitItemStack implements ItemStack {

    private final Materials materials;
    private final org.bukkit.inventory.ItemStack itemStack;

    public BukkitItemStack(Materials materials, org.bukkit.inventory.ItemStack itemStack) {
        this.materials = materials;
        this.itemStack = itemStack;}

    @Override
    public int getAmount() {
        return itemStack.getAmount();
    }

    @Override
    public Material getType() {
        return materials.getMaterial(itemStack.getType().getId());
    }

    public static org.bukkit.inventory.ItemStack toBukkitItemStack(ItemStack itemStack) {
        return new org.bukkit.inventory.ItemStack(toBukkitMaterial(itemStack.getType()), itemStack.getAmount());
    }

    public static org.bukkit.Material toBukkitMaterial(Material material) {
        return org.bukkit.Material.getMaterial(material.getID());
    }
}
