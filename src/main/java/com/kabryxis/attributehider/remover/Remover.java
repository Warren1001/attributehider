package com.kabryxis.attributehider.remover;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.google.common.collect.Sets;
import com.kabryxis.attributehider.AttributeHider;
import com.kabryxis.attributehider.remover.list.BooleanMaterialList;
import com.kabryxis.attributehider.remover.list.EnumSetMaterialList;
import com.kabryxis.attributehider.remover.list.MaterialList;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Remover implements Listener {
	
	private final AttributeHider plugin;
	
	private final Map<ItemFlag, MaterialList> materialLists = new HashMap<>();
	
	public Remover(AttributeHider plugin) {
		this.plugin = plugin;
		
		Set<PacketType> packetTypes;
		if (AttributeHider.DEV) {
			
			packetTypes = PacketType.Play.Server.getInstance().values();
			
		} else {
			
			packetTypes = Sets.newHashSet(PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.ENTITY_EQUIPMENT);
			if (MinecraftVersion.VILLAGE_UPDATE.atOrAbove()) {
				packetTypes.add(PacketType.Play.Server.OPEN_WINDOW_MERCHANT);
			} else {
				packetTypes.add(PacketType.Play.Server.CUSTOM_PAYLOAD);
			}
			
		}
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new RemoverPacketListener(plugin, this, packetTypes));
		setup();
	}
	
	public List<ItemStack> modify(List<ItemStack> items) {
		return items.stream().map(this::modify).collect(Collectors.toList());
	}
	
	public ItemStack[] modify(ItemStack... items) {
		return Stream.of(items).map(this::modify).toArray(ItemStack[]::new);
	}
	
	public ItemStack modify(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) {
			return item;
		}
		
		Material  type  = item.getType();
		
		ItemStack clone = null;
		ItemMeta meta = null;
		
		for (ItemFlag flag : ItemFlag.values()) {
			
			MaterialList materialList = materialLists.get(flag);
			if (materialList.shouldRemove(type)) {
				
				if (clone == null) {
					clone = item.clone();
				}
				if (meta == null) {
					meta = clone.getItemMeta();
				}
				
				meta.addItemFlags(flag);
				clone.setItemMeta(meta);
				
			}
			
		}
		
		if (clone != null) {
			//plugin.getLogger().info(String.format("Modified an item '%s'", clone));
			return clone;
		}
		
		return item;
	}
	
	public void setup() {
		
		materialLists.clear();
		
		for (ItemFlag flag : ItemFlag.values()) {
			materialLists.put(flag, createMaterialList(flag));
		}
		
	}
	
	private MaterialList createMaterialList(ItemFlag flag) {
		
		String               key   = flag.name();
		MaterialList         materialList;
		ConfigurationSection lists = plugin.getConfig().getConfigurationSection("lists");
		
		Object object = lists.get(key, false);
		if (object instanceof Boolean) {
			
			materialList = new BooleanMaterialList((Boolean)object);
			
		} else if (object instanceof String) {
			
			Material material = Material.matchMaterial((String)object);
			if (material == null) {
				plugin.getLogger().warning(String.format("Provided invalid Material for %s: %s", key, object));
				materialList = new BooleanMaterialList(false);
			} else {
				materialList = new EnumSetMaterialList(Collections.singleton(material));
			}
			
		} else if (object instanceof List) {
			
			materialList = new EnumSetMaterialList(parseMaterialCollection(lists.getStringList(key),
					s -> plugin.getLogger().warning(String.format("Provided invalid Material for %s: %s", key, s))));
			
		} else {
			
			plugin.getLogger().warning(String.format("The value given for the list %s was not a true or false, a single Material, or a list of Materials.", key));
			materialList = new BooleanMaterialList(false);
			
		}
		
		return materialList;
	}
	
	private Set<Material> parseMaterialCollection(Collection<String> collection, Consumer<String> invalidMaterialConsumer) {
		Set<Material> materialList = EnumSet.noneOf(Material.class);
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
	
}
