package com.kabryxis.attributehider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.kabryxis.kabutils.spigot.version.Version;
import com.kabryxis.kabutils.spigot.version.wrapper.Wrappable;
import com.kabryxis.kabutils.spigot.version.wrapper.WrapperCache;
import com.kabryxis.kabutils.spigot.version.wrapper.entity.villager.WrappedEntityVillager;
import com.kabryxis.kabutils.spigot.version.wrapper.item.itemstack.WrappedItemStack;
import com.kabryxis.kabutils.spigot.version.wrapper.merchant.merchantrecipe.WrappedMerchantRecipe;
import com.kabryxis.kabutils.spigot.version.wrapper.merchant.merchantrecipelist.WrappedMerchantRecipeList;

public class Remover implements Listener {
	
	private final AttributeHider plugin;
	private final Set<Material> valid = new HashSet<>(Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.CHAINMAIL_HELMET,
			Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
			Material.DIAMOND_BOOTS, Material.WOOD_SWORD, Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.WOOD_SPADE, Material.WOOD_HOE, Material.STONE_SWORD, Material.STONE_PICKAXE,
			Material.STONE_AXE, Material.STONE_SPADE, Material.STONE_HOE, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SPADE, Material.IRON_HOE, Material.GOLD_SWORD,
			Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SPADE,
			Material.DIAMOND_HOE));
	
	private List<Material> materials, enchants;
	private boolean mode = true, hideAttributes = true;
	
	public Remover(AttributeHider plugin) {
		this.plugin = plugin;
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
		boolean b = materials == null ? false : materials.contains(type);
		return mode ? valid.contains(type) || b : b;
	}
	
	public boolean shouldHideEnchants(Material type) {
		return enchants == null ? false : enchants.contains(type);
	}
	
	public void modify(Villager villager, Player player) {
		WrappedEntityVillager<?> handle = WrapperCache.get(WrappedEntityVillager.class);
		handle.setVillager(villager);
		WrappedMerchantRecipeList<?> list = handle.getOffers();
		List<WrappedMerchantRecipe<?>> recipes = list.getRecipes();
		WrappedItemStack<?> item = WrapperCache.get(WrappedItemStack.class);
		for(WrappedMerchantRecipe<?> recipe : recipes) {
			recipe.newInstance(getItem(item, recipe.handleGetBuyingItem1()), getItem(item, recipe.handleGetBuyingItem2()), getItem(item, recipe.handleGetSellingItem()), recipe.getUses(),
					recipe.getMaxUses());
		}
		list.setRecipes(recipes);
		item.cache();
		recipes.forEach(Wrappable::cache);
		list.cache();
		handle.cache();
	}
	
	private Object getItem(WrappedItemStack<?> item, Object handle) {
		item.setHandle(handle);
		ItemStack itemStack = item.getBukkitItemStack();
		modify(itemStack);
		item.setBukkitItemStack(itemStack);
		return item.get();
	}
	
	public void modify(List<ItemStack> items) {
		items.forEach(this::modify);
		
	}
	
	public void modify(ItemStack[] items) {
		for(ItemStack item : items) {
			modify(item);
		}
	}
	
	public void modify(ItemStack item) {
		if(item == null) return;
		Material type = item.getType();
		boolean hideAttributes = shouldRemoveAttributes(type), hideEnchants = shouldHideEnchants(type);
		if(!hideAttributes && !hideEnchants) return;
		ItemMeta meta = item.getItemMeta();
		if(hideAttributes && !meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if(hideEnchants && !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		if(inventory instanceof MerchantInventory && inventory.getHolder() != null) modify((Villager)inventory.getHolder(), (Player)event.getPlayer());
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
		List<String> enchantsList = lists.getStringList("enchants");
		if(!isEmpty(enchantsList)) {
			if(enchants == null) enchants = new ArrayList<>();
			else enchants.clear();
			for(String s : enchantsList) {
				if(s.equals("EXAMPLE_ID")) continue;
				Material type = Material.getMaterial(s);
				if(type != null) enchants.add(type);
				else plugin.getLogger().warning("Found invalid Material in enchants list: " + s);
			}
		}
		// Listeners setup
		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvents(this, plugin);
		if(pm.getPlugin("Shopkeepers") != null) pm.registerEvents(new ShopkeepersListener(this), plugin);
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
					plugin.message("A new version (" + newVersion + ") of AttributeHider is available.");
				}
				
			});
		}
	}
	
	private boolean isEmpty(List<String> list) {
		return list.isEmpty() || (list.size() == 1 && list.get(0).equals("EXAMPLE_ID"));
	}
	
}
