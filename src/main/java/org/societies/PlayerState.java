package org.societies;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.DecimalFormat;

/**
 * Represents a PlayerState
 */
public class PlayerState {
    private Player player;


    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private static final ChatColor[] ARMOR_ORDER = new ChatColor[]{ChatColor.BLACK, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.WHITE, ChatColor.GRAY, ChatColor.AQUA, ChatColor.RED};

    public PlayerState(Player player) {
        this.player = player;
    }

    public String getArmor(String helmetSign, String chestplateSign, String leggingsSign, String bootsSign) {
        StringBuilder armorString = new StringBuilder();
        ChatColor color = ChatColor.BLACK;
        PlayerInventory inventory = player.getInventory();

        ItemStack helmet = inventory.getHelmet();
        ItemStack chestplate = inventory.getChestplate();
        ItemStack leggings = inventory.getLeggings();
        ItemStack boots = inventory.getBoots();

        if (helmet != null) {
            switch (helmet.getType()) {
                case LEATHER_HELMET:
                    color = ARMOR_ORDER[1];
                    break;
                case GOLD_HELMET:
                    color = ARMOR_ORDER[2];
                    break;
                case CHAINMAIL_HELMET:
                    color = ARMOR_ORDER[3];
                    break;
                case IRON_HELMET:
                    color = ARMOR_ORDER[4];
                    break;
                case DIAMOND_HELMET:
                    color = ARMOR_ORDER[5];
                    break;
                default:
                    color = ARMOR_ORDER[6];
            }
        }

        armorString.append(color).append(helmetSign);
        color = ChatColor.BLACK;

        if (chestplate != null) {
            switch (chestplate.getType()) {
                case LEATHER_CHESTPLATE:
                    color = ARMOR_ORDER[1];
                    break;
                case GOLD_CHESTPLATE:
                    color = ARMOR_ORDER[2];
                    break;
                case CHAINMAIL_CHESTPLATE:
                    color = ARMOR_ORDER[3];
                    break;
                case IRON_CHESTPLATE:
                    color = ARMOR_ORDER[4];
                    break;
                case DIAMOND_CHESTPLATE:
                    color = ARMOR_ORDER[5];
                    break;
                default:
                    color = ARMOR_ORDER[6];
            }
        }

        armorString.append(color).append(chestplateSign);
        color = ChatColor.BLACK;

        if (leggings != null) {
            switch (leggings.getType()) {
                case LEATHER_LEGGINGS:
                    color = ARMOR_ORDER[1];
                    break;
                case GOLD_LEGGINGS:
                    color = ARMOR_ORDER[2];
                    break;
                case CHAINMAIL_LEGGINGS:
                    color = ARMOR_ORDER[3];
                    break;
                case IRON_LEGGINGS:
                    color = ARMOR_ORDER[4];
                    break;
                case DIAMOND_LEGGINGS:
                    color = ARMOR_ORDER[5];
                    break;
                default:
                    color = ARMOR_ORDER[6];
            }
        }

        armorString.append(color).append(leggingsSign);
        color = ChatColor.BLACK;

        if (boots != null) {
            switch (boots.getType()) {
                case LEATHER_BOOTS:
                    color = ARMOR_ORDER[1];
                    break;
                case GOLD_BOOTS:
                    color = ARMOR_ORDER[2];
                    break;
                case CHAINMAIL_BOOTS:
                    color = ARMOR_ORDER[3];
                    break;
                case IRON_BOOTS:
                    color = ARMOR_ORDER[4];
                    break;
                case DIAMOND_BOOTS:
                    color = ARMOR_ORDER[5];
                    break;
                default:
                    color = ARMOR_ORDER[6];
            }
        }

        armorString.append(color).append(bootsSign);

        return armorString.length() == 0 ? ChatColor.BLACK + "Empty" : armorString.toString();
    }

    public String getWeapons(String sword, String bow, String arrow) {
        ItemStack[] contents = player.getInventory().getContents();
        StringBuilder weapons = new StringBuilder();

        for (ItemStack itemStack : contents) {
            if (itemStack == null) {
                continue;
            }

            String type;
            ChatColor color;

            switch (itemStack.getType()) {
                case WOOD_SWORD:
                    type = sword;
                    color = ChatColor.GOLD;
                    break;
                case GOLD_SWORD:
                    type = sword;
                    color = ChatColor.YELLOW;
                    break;
                case IRON_SWORD:
                    type = sword;
                    color = ChatColor.WHITE;
                    break;
                case DIAMOND_SWORD:
                    type = sword;
                    color = ChatColor.WHITE;
                    break;
                case BOW:
                    type = bow;
                    color = ChatColor.GOLD;
                    break;
                case ARROW:
                    type = arrow;
                    color = ChatColor.GOLD;
                    break;
                default:
                    return "Empty";
            }

            if (type != null) {
                weapons.append(color).append(type);
                int amount = itemStack.getAmount();
                if (amount > 1) {
                    weapons.append(amount);
                }
            }
        }

        return weapons.length() == 0 ? ChatColor.BLACK + "Empty" : weapons.toString();
    }

    //todo
//    public String getFood(String format) {
//        ItemStack[] contents = player.getInventory().getContents();
//
//        double food = 0;
//
//        for (ItemStack itemStack : contents) {
//            if (itemStack == null) {
//                continue;
//            }
//
//            int value = itemStack.getType().getFoodLevel();
//
//            food += value;
//        }
//
//        food /= 2;
//
//        return String.format(format, DECIMAL_FORMAT.format(food));
//    }

    public String getHealth() {
        double health = player.getHealth();
        return getBar(health);
    }

    public String getHunger() {
        int hunger = player.getFoodLevel();
        return getBar(hunger);
    }

    private static String getBar(double amount) {
        StringBuilder bar = new StringBuilder();

        if (amount > 0.80 * amount) {
            bar.append(ChatColor.GREEN);
        } else if (amount >= 0.45 * amount) {
            bar.append(ChatColor.GOLD);
        } else {
            bar.append(ChatColor.RED);
        }

        for (int i = 0; i < amount; i++) {
            bar.append('|');
        }

        return bar.toString();
    }
}
