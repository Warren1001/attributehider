package com.kabryxis.attributehider;

import org.bukkit.Material;

import java.util.*;
import java.util.function.Consumer;

public class KMaterial {
	
	private static final Set<Material> ATTRIBUTEABLE_MATERIALS = new HashSet<>();
	private static final Set<Material> POTIONABLE_MATERIALS    = new HashSet<>();
	private static final Set<Material> UNBREAKABLE_MATERIALS   = new HashSet<>();
	
	public static final Material BOW                  = add(Material.BOW, false);
	public static final Material CHAINMAIL_BOOTS      = add(Material.CHAINMAIL_BOOTS, true);
	public static final Material CHAINMAIL_CHESTPLATE = add(Material.CHAINMAIL_CHESTPLATE, true);
	public static final Material CHAINMAIL_HELMET     = add(Material.CHAINMAIL_HELMET, true);
	public static final Material CHAINMAIL_LEGGINGS   = add(Material.CHAINMAIL_LEGGINGS, true);
	public static final Material CROSSBOW             = add(findFirstMaterial("CROSSBOW"), false);
	public static final Material DIAMOND_AXE          = add(findFirstMaterial("DIAMOND_AXE"), true);
	public static final Material DIAMOND_BOOTS        = add(findFirstMaterial("DIAMOND_BOOTS"), true);
	public static final Material DIAMOND_CHESTPLATE   = add(findFirstMaterial("DIAMOND_CHESTPLATE"), true);
	public static final Material DIAMOND_HELMET       = add(findFirstMaterial("DIAMOND_HELMET"), true);
	public static final Material DIAMOND_HOE          = add(findFirstMaterial("DIAMOND_HOE"), true);
	public static final Material DIAMOND_LEGGINGS     = add(findFirstMaterial("DIAMOND_LEGGINGS"), true);
	public static final Material DIAMOND_PICKAXE      = add(findFirstMaterial("DIAMOND_PICKAXE"), true);
	public static final Material DIAMOND_SHOVEL       = add(findFirstMaterial("DIAMOND_SHOVEL", "DIAMOND_SPADE"), true);
	public static final Material DIAMOND_SWORD        = add(findFirstMaterial("DIAMOND_SWORD"), true);
	public static final Material FISHING_ROD          = add(Material.FISHING_ROD, false);
	public static final Material GOLDEN_AXE           = add(findFirstMaterial("GOLDEN_AXE", "GOLD_AXE"), true);
	public static final Material GOLDEN_BOOTS         = add(findFirstMaterial("GOLDEN_BOOTS", "GOLD_BOOTS"), true);
	public static final Material GOLDEN_CHESTPLATE    = add(findFirstMaterial("GOLDEN_CHESTPLATE", "GOLD_CHESTPLATE"), true);
	public static final Material GOLDEN_HELMET        = add(findFirstMaterial("GOLDEN_HELMET", "GOLD_HELMET"), true);
	public static final Material GOLDEN_HOE           = add(findFirstMaterial("GOLDEN_HOE", "GOLD_HOE"), true);
	public static final Material GOLDEN_LEGGINGS      = add(findFirstMaterial("GOLDEN_LEGGINGS", "GOLD_LEGGINGS"), true);
	public static final Material GOLDEN_PICKAXE       = add(findFirstMaterial("GOLDEN_PICKAXE", "GOLD_PICKAXE"), true);
	public static final Material GOLDEN_SHOVEL        = add(findFirstMaterial("GOLDEN_SHOVEL", "GOLD_SPADE"), true);
	public static final Material GOLDEN_SWORD         = add(findFirstMaterial("GOLDEN_SWORD", "GOLD_SWORD"), true);
	public static final Material IRON_AXE             = add(findFirstMaterial("IRON_AXE"), true);
	public static final Material IRON_BOOTS           = add(findFirstMaterial("IRON_BOOTS"), true);
	public static final Material IRON_CHESTPLATE      = add(findFirstMaterial("IRON_CHESTPLATE"), true);
	public static final Material IRON_HELMET          = add(findFirstMaterial("IRON_HELMET"), true);
	public static final Material IRON_HOE             = add(findFirstMaterial("IRON_HOE"), true);
	public static final Material IRON_LEGGINGS        = add(findFirstMaterial("IRON_LEGGINGS"), true);
	public static final Material IRON_PICKAXE         = add(findFirstMaterial("IRON_PICKAXE"), true);
	public static final Material IRON_SHOVEL          = add(findFirstMaterial("IRON_SHOVEL", "IRON_SPADE"), true);
	public static final Material IRON_SWORD           = add(findFirstMaterial("IRON_SWORD"), true);
	public static final Material LEATHER_BOOTS        = add(Material.LEATHER_BOOTS, true);
	public static final Material LEATHER_CHESTPLATE   = add(Material.LEATHER_CHESTPLATE, true);
	public static final Material LEATHER_HELMET       = add(Material.LEATHER_HELMET, true);
	public static final Material LEATHER_LEGGINGS     = add(Material.LEATHER_LEGGINGS, true);
	public static final Material LINGERING_POTION     = potionable(findFirstMaterial("LINGERING_POTION")); // 1.9
	public static final Material NETHERITE_AXE        = add(findFirstMaterial("NETHERITE_AXE"), true); // 1.16
	public static final Material NETHERITE_BOOTS      = add(findFirstMaterial("NETHERITE_BOOTS"), true); // 1.16
	public static final Material NETHERITE_CHESTPLATE = add(findFirstMaterial("NETHERITE_CHESTPLATE"), true); // 1.16
	public static final Material NETHERITE_HELMET     = add(findFirstMaterial("NETHERITE_HELMET"), true); // 1.16
	public static final Material NETHERITE_HOE        = add(findFirstMaterial("NETHERITE_HOE"), true); // 1.16
	public static final Material NETHERITE_LEGGINGS   = add(findFirstMaterial("NETHERITE_LEGGINGS"), true); // 1.16
	public static final Material NETHERITE_PICKAXE    = add(findFirstMaterial("NETHERITE_PICKAXE"), true); // 1.16
	public static final Material NETHERITE_SHOVEL     = add(findFirstMaterial("NETHERITE_SHOVEL"), true); // 1.16
	public static final Material NETHERITE_SWORD      = add(findFirstMaterial("NETHERITE_SWORD"), true); // 1.16
	public static final Material POTION               = potionable(Material.POTION);
	public static final Material SHEARS               = add(Material.SHEARS, false);
	public static final Material SHIELD               = add(findFirstMaterial("SHIELD"), false); // 1.9
	public static final Material SPLASH_POTION        = potionable(findFirstMaterial("SPLASH_POTION")); // 1.9
	public static final Material STONE_AXE            = add(Material.STONE_AXE, true);
	public static final Material STONE_HOE            = add(Material.STONE_HOE, true);
	public static final Material STONE_PICKAXE        = add(Material.STONE_PICKAXE, true);
	public static final Material STONE_SHOVEL         = add(findFirstMaterial("STONE_SHOVEL", "STONE_SPADE"), true);
	public static final Material STONE_SWORD          = add(Material.STONE_SWORD, true);
	public static final Material TRIDENT              = add(findFirstMaterial("TRIDENT"), true); // 1.13
	public static final Material TURTLE_HELMET        = add(findFirstMaterial("TURTLE_HELMET"), true); // 1.13
	public static final Material WOODEN_AXE           = add(findFirstMaterial("WOODEN_AXE", "WOOD_AXE"), true);
	public static final Material WOODEN_HOE           = add(findFirstMaterial("WOODEN_HOE", "WOOD_HOE"), true);
	public static final Material WOODEN_PICKAXE       = add(findFirstMaterial("WOODEN_PICKAXE", "WOOD_PICKAXE"), true);
	public static final Material WOODEN_SHOVEL        = add(findFirstMaterial("WOODEN_SHOVEL", "WOOD_SPADE"), true);
	public static final Material WOODEN_SWORD         = add(findFirstMaterial("WOODEN_SWORD", "WOOD_SWORD"), true);
	
	public static boolean isAttributeable(Material material) {
		return ATTRIBUTEABLE_MATERIALS.contains(material);
	}
	
	public static boolean isPotionable(Material material) {
		return POTIONABLE_MATERIALS.contains(material);
	}
	
	public static boolean isUnbreakable(Material material) {
		return UNBREAKABLE_MATERIALS.contains(material);
	}
	
	public static Set<Material> getAttributeableMaterials() {
		return new HashSet<>(ATTRIBUTEABLE_MATERIALS);
	}
	
	public static Set<Material> getPotionableMaterials() {
		return new HashSet<>(POTIONABLE_MATERIALS);
	}
	
	public static Set<Material> getUnbreakableMaterials() {
		return new HashSet<>(UNBREAKABLE_MATERIALS);
	}
	
	public static Set<Material> parseMaterialCollection(Collection<String> collection, Consumer<String> invalidMaterialConsumer) {
		Set<Material> materialList = new HashSet<>(collection.size());
		for (String string : collection) {
			Material material = Material.matchMaterial(string);
			if (material == null) {
				invalidMaterialConsumer.accept(string);
			} else {
				materialList.add(material);
			}
		}
		return materialList;
	}
	
	private static Material findFirstMaterial(String... materials) {
		for (String materialString : materials) {
			Material material = Material.matchMaterial(materialString);
			if (material != null) return material;
		}
		return Material.AIR;
	}
	
	private static Material potionable(Material material) {
		if (material != Material.AIR) POTIONABLE_MATERIALS.add(material);
		return material;
	}
	
	private static Material add(Material material, boolean add) {
		if (material == null) return null;
		if (add) ATTRIBUTEABLE_MATERIALS.add(material);
		UNBREAKABLE_MATERIALS.add(material);
		return material;
	}
	
}
