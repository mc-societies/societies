package net.catharos.societies.bridge;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;

/**
 * Represents a Material
 */
public interface Material {

    int getID();

    String getName();

    String getInternalName();

    boolean isFood();

    int getFoodLevel();

    boolean isBoot();

    boolean isHelmet();

    boolean isChestplate();

    boolean isLeggings();

    boolean isWeapon();

    public static final Attribute<Material, Integer> MATERIAL_ID = new SimpleAttribute<Material, Integer>("material_id") {
        @Override
        public Integer getValue(Material material) { return material.getID(); }
    };

    public static final Attribute<Material, String> LOWER_MATERIAL_NAME = new SimpleAttribute<Material, String>("material_name") {
        @Override
        public String getValue(Material material) { return material.getName().toLowerCase(); }
    };

}
