package net.catharos.societies.bridge;

import com.google.inject.Inject;
import com.googlecode.cqengine.CQEngine;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.query.Query;

import java.util.Collection;

import static com.googlecode.cqengine.query.QueryFactory.contains;
import static com.googlecode.cqengine.query.QueryFactory.equal;

/** Represents a Material */
public class Materials {

    public static final int WOOD_SWORD = 268;
    public static final int GOLD_SWORD = 283;
    public static final int IRON_SWORD = 267;
    public static final int DIAMOND_SWORD = 276;
    public static final int BOW = 261;
    public static final int ARROW = 262;
    public static final int LEATHER_BOOTS = 301;
    public static final int GOLD_BOOTS = 317;
    public static final int CHAINMAIL_BOOTS = 305;
    public static final int IRON_BOOTS = 309;
    public static final int DIAMOND_BOOTS = 313;
    public static final int LEATHER_HELMET = 298;
    public static final int GOLD_HELMET = 314;
    public static final int CHAINMAIL_HELMET = 302;
    public static final int IRON_HELMET = 306;
    public static final int DIAMOND_HELMET = 310;
    public static final int LEATHER_CHESTPLATE = 299;
    public static final int GOLD_CHESTPLATE = 315;
    public static final int CHAINMAIL_CHESTPLATE = 303;
    public static final int IRON_CHESTPLATE = 307;
    public static final int DIAMOND_CHESTPLATE = 311;
    public static final int LEATHER_LEGGINGS = 300;
    public static final int GOLD_LEGGINGS = 316;
    public static final int CHAINMAIL_LEGGINGS = 304;
    public static final int IRON_LEGGINGS = 308;
    public static final int DIAMOND_LEGGINGS = 312;
    private final IndexedCollection<Material> materials = CQEngine.newInstance();

    {
        materials.addIndex(SuffixTreeIndex.onAttribute(Material.LOWER_MATERIAL_NAME));
        materials.addIndex(HashIndex.onAttribute(Material.MATERIAL_ID));
    }

    @Inject
    public Materials(Collection<Material> materials) {
        this.materials.addAll(materials);
    }

    public Material getMaterial(int id) {
        Query<Material> query = equal(Material.MATERIAL_ID, id);
        return materials.retrieve(query).uniqueResult();
    }

    public Material getMaterial(String name) {
        Query<Material> query = contains(Material.LOWER_MATERIAL_NAME, name.toLowerCase());
        return materials.retrieve(query).uniqueResult();
    }
}
