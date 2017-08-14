package com.kabryxis.attributehider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.kabryxis.attributehider.remover.Remover;

public class AttributeHider extends JavaPlugin implements Listener {
	
	private final static int CONFIG_VERSION = 1;
	
	private final static Set<Material> valid = new HashSet<>(Arrays.asList(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS,
			Material.IRON_BOOTS, Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.WOOD_SWORD, Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.WOOD_SPADE, Material.WOOD_HOE, Material.STONE_SWORD,
			Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SPADE, Material.STONE_HOE, Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SPADE, Material.IRON_HOE,
			Material.GOLD_SWORD, Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE,
			Material.DIAMOND_SPADE, Material.DIAMOND_HOE));
	private final static List<Material> materials = new ArrayList<>();
	private final static Remover remover;
	
	private static boolean mode = true;
	
	static {
		Remover r;
		try {
			r = (Remover)Class.forName("com.kabryxis.attributehider.remover.impl.Remover" + Version.STRING.replaceAll("[a-zA-Z]", "")).newInstance();
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			r = null;
		}
		remover = r;
	}
	
	private static boolean shouldBeModified(Material type) {
		boolean b = materials.contains(type);
		return mode ? valid.contains(type) || b : b;
	}
	
	public static boolean shouldBeModified(ItemStack item) {
		return item == null ? false : shouldBeModified(item.getType());
	}
	
	@SuppressWarnings("deprecation")
	public static boolean shouldBeModified(int id) {
		return shouldBeModified(Material.getMaterial(id));
	}
	
	private final SpigetUpdate updater = new SpigetUpdate(this, 10604);
	private final PluginManager pm = getServer().getPluginManager();
	
	private boolean isRegistered = false;
	
	@Override
	public void onEnable() {
		updater.setVersionComparator(VersionComparator.SEM_VER);
		if(remover == null) {
			disablePlugin("This plugin does not support your Minecraft server version.");
			return;
		}
		if(pm.getPlugin("ProtocolLib") == null) {
			disablePlugin("This plugin requires the plugin ProtocolLib to be installed.");
			return;
		}
		File current = new File(getDataFolder(), "config.yml"), backup = new File(getDataFolder(), "config-backup.yml");
		if(current.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(current);
			if(config.getInt("version") != CONFIG_VERSION) {
				current.renameTo(backup);
				saveDefaultConfig();
				disablePlugin("This plugin's configuration is outdated. Your old values have been backed up and the new configuration has been generated. Please update the new configuration.");
				return;
			}
		}
		else saveDefaultConfig();
		if(backup.exists())
			message("An older configuration version has been detected. This is a reminder to update the values of the new configuration. Delete the backup file to get rid of this notification.");
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT) {
			
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				if(packet.getType() == PacketType.Play.Server.WINDOW_ITEMS) {
					if(Version.VERSION > Version.v1_10_R1) remover.remove(packet.getItemListModifier().read(0));
					else remover.remove(packet.getItemArrayModifier().read(0));
				}
				else remover.remove(packet.getItemModifier().read(0));
			}
			
		});
		setup();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("attributehider")) {
			if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
				if(!sender.hasPermission(getConfig().getString("command.permission"))) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.no-permission")));
					return true;
				}
				reloadConfig();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.reloaded")));
				setup();
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		if(inventory instanceof MerchantInventory) remover.remove((Villager)inventory.getHolder(), (Player)event.getPlayer());
	}
	
	private void setup() {
		// Materials setup
		ConfigurationSection section = getConfig().getConfigurationSection("list");
		mode = section.getBoolean("mode");
		materials.clear();
		List<String> list = section.getStringList("list");
		if(!mode && (list.isEmpty() || (list.size() == 1 && list.get(0).equals("EXAMPLE_ID")))) {
			disablePlugin("You have your list set to custom but have provided no material IDs. This plugin will not be modifying any items as a result.");
			return;
		}
		for(String s : list) {
			if(s.equals("EXAMPLE_ID")) continue;
			Material type = Material.getMaterial(s);
			if(type != null) materials.add(type);
			else getLogger().warning("Found invalid Material in whitelist: " + s);
		}
		// Villagers setup
		if(getConfig().getBoolean("modify-villagers")) {
			if(!isRegistered) {
				pm.registerEvents(this, this);
				isRegistered = true;
			}
		}
		else if(isRegistered) {
			HandlerList.unregisterAll((Listener)this);
			isRegistered = false;
		}
		// Updater setup
		ConfigurationSection update = getConfig().getConfigurationSection("update");
		if(update.getBoolean("check")) {
			updater.checkForUpdate(new UpdateCallback() {
				
				@Override
				public void upToDate() {}
				
				@Override
				public void updateAvailable(String newVersion, String url, boolean canDownload) {
					message(update.getBoolean("download")
							? (canDownload && updater.downloadUpdate() ? "A new version (" + newVersion + ") of AttributeHider has been downloaded. It will be loaded upon server restart."
									: "A new version (" + newVersion + ") of AttributeHider is available but is unable to be downloaded at this time.")
							: "A new version (" + newVersion + ") of AttributeHider is available.");
				}
				
			});
		}
	}
	
	private void message(String message) {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[AttributeHider] " + message);
	}
	
	private void disablePlugin(String message) {
		message(message + " Disabling.");
		pm.disablePlugin(this);
	}
	
}
