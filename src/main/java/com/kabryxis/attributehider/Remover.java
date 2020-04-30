package com.kabryxis.attributehider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.google.common.collect.Sets;
import com.kabryxis.kabutils.data.Lists;
import com.kabryxis.kabutils.spigot.inventory.itemstack.Items;
import com.kabryxis.kabutils.spigot.version.Version;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.villager.WrappedEntityVillager;
import com.kabryxis.kabutils.spigot.version.wrapper.item.itemstack.WrappedItemStack;
import com.kabryxis.kabutils.spigot.version.wrapper.merchant.merchantrecipe.WrappedMerchantRecipe;
import com.kabryxis.kabutils.spigot.version.wrapper.merchant.merchantrecipelist.WrappedMerchantRecipeList;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Remover implements Listener {
	
	private final AttributeHider plugin;
	private final Set<Material> valid = Sets.newHashSet(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET,
			Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.STONE_SWORD, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_HOE,
			Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE,
			Material.DIAMOND_AXE, Material.DIAMOND_HOE);
	
	private List<Material> materials, enchants;
	private boolean mode = true, hideAttributes = true, hideAllEnchants = false, hideUnbreakable = false;
	
	public Remover(AttributeHider plugin) {
		this.plugin = plugin;
		if(Version.VERSION.isVersionAtLeast(Version.v1_13_R2)) {
			valid.addAll(Arrays.asList(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
					Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE,
					Material.IRON_SHOVEL, Material.STONE_SHOVEL, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,
					Material.GOLDEN_SWORD, Material.DIAMOND_SHOVEL, Material.TURTLE_HELMET, Material.TRIDENT, Material.CROSSBOW));
		} else {
			valid.addAll(Arrays.asList(Material.matchMaterial("GOLD_HELMET"), Material.matchMaterial("GOLD_CHESTPLATE"), Material.matchMaterial("GOLD_LEGGINGS"),
					Material.matchMaterial("GOLD_BOOTS"), Material.matchMaterial("WOOD_SWORD"), Material.matchMaterial("WOOD_PICKAXE"),
					Material.matchMaterial("WOOD_AXE"), Material.matchMaterial("WOOD_SPADE"), Material.matchMaterial("WOOD_HOE"),
					Material.matchMaterial("STONE_SPADE"), Material.matchMaterial("IRON_SPADE"), Material.matchMaterial("DIAMOND_SPADE"),
					Material.matchMaterial("GOLD_SWORD"), Material.matchMaterial("GOLD_PICKAXE"), Material.matchMaterial("GOLD_AXE"),
					Material.matchMaterial("GOLD_SPADE"), Material.matchMaterial("GOLD_HOE")));
		}
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT) {
			
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				if(packet.getType() == PacketType.Play.Server.WINDOW_ITEMS) {
					if(Version.VERSION.isVersionAtLeast(Version.v1_11_R1)) {
						StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
						List<ItemStack> items = modifier.read(0);
						modify(items);
						modifier.write(0, items);
					}
					else {
						StructureModifier<ItemStack[]> modifier = packet.getItemArrayModifier();
						ItemStack[] items = modifier.read(0);
						modify(items);
						modifier.write(0, items);
					}
				}
				else {
					StructureModifier<ItemStack> modifier = packet.getItemModifier();
					ItemStack item = modifier.read(0);
					modify(item);
					modifier.write(0, item);
				}
			}
			
		});
		setup();
	}
	
	public boolean shouldRemoveAttributes(Material type) {
		if(!hideAttributes) return false;
		boolean b = materials != null && materials.contains(type);
		return mode ? valid.contains(type) || b : b;
	}
	
	public boolean shouldHideEnchants(Material type) {
		return hideAllEnchants || (enchants != null && enchants.contains(type));
	}
	
	public boolean shouldHidePotionEffects() {
		return plugin.getConfig().getBoolean("lists.potions", false);
	}
	
	public boolean shouldHideUnbreakableTag() {
		return plugin.getConfig().getBoolean("lists.unbreakable", false);
	}
	
	public void modify(Villager villager) {
		WrappedEntityVillager entityVillager = WrappedEntityVillager.newInstance(villager);
		WrappedMerchantRecipeList merchantRecipeList = entityVillager.getOffers();
		WrappedItemStack item = WrappedItemStack.newInstance();
		merchantRecipeList.setRecipes(merchantRecipeList.getRecipes().stream().map(recipe -> WrappedMerchantRecipe.newInstance(getItem(item, recipe.getBuyingItem1()), getItem(item, recipe.getBuyingItem2()),
				getItem(item, recipe.getSellingItem()), recipe.getUses(), recipe.getMaxUses())).collect(Collectors.toList()));
	}
	
	private Object getItem(WrappedItemStack item, Object handle) {
		item.setHandle(handle);
		ItemStack itemStack = item.getBukkitItemStack();
		if(itemStack.getType() == Material.AIR) return handle;
		modify(itemStack);
		item.setHandle(itemStack);
		return item.getHandle();
	}
	
	public void modify(List<ItemStack> items) {
		items.forEach(this::modify);
	}
	
	public void modify(ItemStack[] items) {
		com.kabryxis.kabutils.data.Arrays.forEach(items, this::modify);
	}
	
	public boolean modify(ItemStack item) {
		if(!Items.exists(item)) return false;
		Material type = item.getType();
		if(!shouldRemoveAttributes(type)) return false;
		ItemMeta meta = item.getItemMeta();
		boolean modified = false;
		if(shouldRemoveAttributes(type) && !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
			modified = true;
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		}
		if(shouldHideUnbreakableTag() && !meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			modified = true;
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if(shouldHideEnchants(type) && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
			modified = true;
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		if(shouldHidePotionEffects() && (type == Material.POTION || (Version.VERSION.isVersionAtLeast(Version.v1_9_R1)
				&& (type == Material.SPLASH_POTION || type == Material.LINGERING_POTION))) && !meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) {
			modified = true;
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		}
		if(modified) item.setItemMeta(meta);
		return modified;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		if(inventory instanceof MerchantInventory && inventory.getHolder() instanceof Villager) modify((Villager)inventory.getHolder());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if(modify(item)) event.setCurrentItem(item);
	}
	
	public void setup() {
		// Materials setup
		ConfigurationSection lists = plugin.getConfig().getConfigurationSection("lists");
		ConfigurationSection attributes = lists.getConfigurationSection("attributes");
		mode = attributes.getBoolean("mode");
		List<String> attributesList = attributes.getStringList("list");
		if(isEmpty(attributesList)) {
			if(!mode) {
				plugin.message("You have your list set to custom but have provided no material IDs. As a result, this plugin will not remove attributes from items.");
				hideAttributes = false;
			}
		}
		else {
			if(materials == null) materials = new ArrayList<>();
			else materials.clear();
			for(String s : attributesList) {
				if(s.equals("EXAMPLE_ID")) continue;
				Material type = Material.getMaterial(s);
				if(type != null) materials.add(type);
				else plugin.getLogger().warning("Found invalid Material in attributes list: " + s);
			}
		}
		Object enchantsObj = lists.get("enchants");
		if(enchantsObj instanceof Boolean) {
			if((Boolean)enchantsObj) hideAllEnchants = true;
		}
		else if(enchantsObj instanceof List) {
			List<String> enchantsList = Lists.convert((List<?>)enchantsObj, String.class);
			if(!isEmpty(enchantsList)) {
				if(enchants == null) enchants = new ArrayList<>();
				else enchants.clear();
				for(String s : enchantsList) {
					if(s.equals("EXAMPLE_ID")) continue;
					Material type = Material.matchMaterial(s);
					if(type != null) enchants.add(type);
					else plugin.getLogger().warning("Found invalid Material in enchants list: " + s);
				}
			}
		}
		// Listeners setup
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		// Updater setup
		if(plugin.getConfig().getBoolean("check-updates")) {
			if(!Version.VERSION.isVersionAtLeast(Version.v1_8_R2)) {
				plugin.message("This Spigot version does not support the updater. If you wish to use the updater, please update to Spigot 1.8.3 or above.");
				return;
			}
			SpigetUpdate updater = new SpigetUpdate(plugin, 10604);
			updater.setVersionComparator(VersionComparator.SEM_VER);
			updater.checkForUpdate(new UpdateCallback() {
				
				@Override
				public void upToDate() {}
				
				@Override
				public void updateAvailable(String newVersion, String url, boolean canDownload) {
					plugin.message(String.format("A new version (%s) of AttributeHider is available.", newVersion));
				}
				
			});
		}
	}
	
	private boolean isEmpty(List<String> list) {
		return list.isEmpty() || (list.size() == 1 && list.get(0).equals("EXAMPLE_ID"));
	}
	
}
