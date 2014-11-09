package net.catharos.societies.bukkit.bridge;

import net.catharos.societies.bridge.Inventory;
import net.catharos.societies.bridge.ItemStack;
import net.catharos.societies.bridge.Materials;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents a BukkitInventory
 */
public class BukkitInventory implements Inventory {

    private final Materials materials;
    private final PlayerInventory inventory;

    public BukkitInventory(Materials materials, PlayerInventory inventory) {
        this.materials = materials;
        this.inventory = inventory;
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] itemStacks = new ItemStack[inventory.getSize()];

        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = new BukkitItemStack(materials, inventory.getContents()[i]);

        }

        return itemStacks;
    }

    @Override
    public void remove(ItemStack itemStack) {
        inventory.remove(BukkitItemStack.toBukkitItemStack(itemStack));
    }

    @Override
    public ItemStack getHelmet() {
        return new BukkitItemStack(materials, inventory.getHelmet());
    }

    @Override
    public ItemStack getLeggings() {
        return new BukkitItemStack(materials, inventory.getLeggings());
    }

    @Override
    public ItemStack getChestplate() {
        return new BukkitItemStack(materials, inventory.getChestplate());
    }

    @Override
    public ItemStack getBoots() {
        return new BukkitItemStack(materials, inventory.getBoots());
    }
}
