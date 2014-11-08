package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.Inventory;
import net.catharos.societies.bridge.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents a BukkitInventory
 */
public class BukkitInventory implements Inventory {

    private final PlayerInventory inventory;

    public BukkitInventory(PlayerInventory inventory) {this.inventory = inventory;}

    @Override
    public ItemStack[] getContents() {
        ItemStack[] itemStacks = new ItemStack[inventory.getSize()];

        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = new BukkitItemStack(inventory.getContents()[i]);

        }
        return itemStacks;
    }

    @Override
    public void remove(ItemStack itemStack) {
        inventory.remove(BukkitItemStack.toBukkitItemStack(itemStack));
    }

    @Override
    public ItemStack getHelmet() {
        return new BukkitItemStack(inventory.getHelmet());
    }

    @Override
    public ItemStack getLeggings() {
        return new BukkitItemStack(inventory.getLeggings());
    }

    @Override
    public ItemStack getChestplate() {
        return new BukkitItemStack(inventory.getChestplate());
    }

    @Override
    public ItemStack getBoots() {
        return new BukkitItemStack(inventory.getBoots());
    }
}
