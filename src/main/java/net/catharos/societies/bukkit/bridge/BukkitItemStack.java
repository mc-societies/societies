package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.ItemStack;
import net.catharos.societies.bridge.Material;

/**
 * Represents a BukkitItemStack
 */
public class BukkitItemStack implements ItemStack {

    private final org.bukkit.inventory.ItemStack itemStack;

    public BukkitItemStack(org.bukkit.inventory.ItemStack itemStack) {this.itemStack = itemStack;}

    @Override
    public int getAmount() {
        return itemStack.getAmount();
    }

    @Override
    public Material getType() {
        return Material.getMaterial(itemStack.getType().getId());
    }

    public static org.bukkit.inventory.ItemStack toBukkitItemStack(ItemStack itemStack) {
        return new org.bukkit.inventory.ItemStack(org.bukkit.Material
                .getMaterial(itemStack.getType().getID()), itemStack.getAmount());
    }
}
