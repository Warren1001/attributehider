package com.kabryxis.attributehider.remover;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.Sets;
import com.kabryxis.attributehider.AttributeHider;
import com.kabryxis.attributehider.util.KMaterial;
import com.kabryxis.attributehider.util.UpdateChecker;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Remover implements Listener {
	
	private final AttributeHider plugin;
	
	private Set<Material> removeAttributesFrom;
	private Set<Material> removeEnchantsFrom;
	private Set<Material> removePotionEffectsFrom;
	private Set<Material> removeUnbreakableFrom;
	
	public Remover(AttributeHider plugin) {
		this.plugin = plugin;
		
		Set<PacketType> packetTypes = Sets.newHashSet(PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT);
		if (MinecraftVersion.VILLAGE_UPDATE.atOrAbove()) {
			packetTypes.add(PacketType.Play.Server.OPEN_WINDOW_MERCHANT);
		} else {
			packetTypes.add(PacketType.Play.Server.CUSTOM_PAYLOAD);
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new RemoverPacketListener(plugin, this, packetTypes));
		setup();
		
		if (plugin.getConfig().getBoolean("check-updates")) {
			UpdateChecker.check(plugin, 10604, response -> {
				if (response == UpdateChecker.ResponseType.NEW_VERSION) {
					plugin.getLogger().info("There is a new version of AttributeHider available!");
				} else if (response == UpdateChecker.ResponseType.ERROR) {
					plugin.getLogger().warning("Unable to check for updates.");
				}
			});
		}
	}
	
	public boolean shouldRemoveAttributes(Material type) {
		return removeAttributesFrom != null && removeAttributesFrom.contains(type);
	}
	
	public boolean shouldHideEnchants(Material type) {
		return removeEnchantsFrom != null && removeEnchantsFrom.contains(type);
	}
	
	public boolean shouldHidePotionEffects(Material type) {
		return removePotionEffectsFrom != null && removePotionEffectsFrom.contains(type);
	}
	
	public boolean shouldHideUnbreakableTag(Material type) {
		return removeUnbreakableFrom != null && removeUnbreakableFrom.contains(type);
	}
	
	public List<ItemStack> modify(List<ItemStack> items) {
		items.forEach(this::modify);
		return items;
	}
	
	public ItemStack[] modify(ItemStack... items) {
		Stream.of(items).forEach(this::modify);
		return items;
	}
	
	public ItemStack modify(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return item;
		}
		Material type = item.getType();
		ItemMeta meta = item.getItemMeta();
		// Can't decide if I should create an ItemMeta even if an ItemStack does not get modified.
		// Slightly increases packet size for packets that are modified by creating empty ItemMeta for
		// items that do not already have an ItemMeta? Makes code a lil more tedious though.
		if (shouldRemoveAttributes(type)) {
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		if (shouldHideEnchants(type)) {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		if (shouldHidePotionEffects(type)) {
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		}
		if (shouldHideUnbreakableTag(type)) {
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public void setup() {
		
		removeAttributesFrom = buildSet("attributes", true, KMaterial::getAttributeableMaterials);
		removeEnchantsFrom = buildSet("enchants", false, () -> Sets.newHashSet(Material.values()));
		removePotionEffectsFrom = buildSet("potions", false, KMaterial::getPotionableMaterials);
		removeUnbreakableFrom = buildSet("unbreakable", false, KMaterial::getUnbreakableMaterials);
		
	}
	
	private Set<Material> buildSet(String type, Object defaultValue, Supplier<Set<Material>> setIfAll) {
		
		Set<Material>        materials = null;
		ConfigurationSection lists     = plugin.getConfig().getConfigurationSection("lists");
		
		Object object = lists.get(type, defaultValue);
		if (object instanceof Boolean) {
			
			if (((Boolean)object)) {
				materials = setIfAll.get();
			}
			
		} else if (object instanceof String) {
			
			Material material = Material.matchMaterial((String)object);
			if (material == null) {
				plugin.getLogger().warning(String.format("Provided invalid Material for %s: %s", type, object));
			} else {
				materials = Collections.singleton(material);
			}
			
		} else if (object instanceof List) {
			
			materials = KMaterial.parseMaterialCollection(lists.getStringList(type),
					s -> plugin.getLogger().warning(String.format("Provided invalid Material for %s: %s", type, s)));
			
		}
		
		return materials;
	}
	
}
