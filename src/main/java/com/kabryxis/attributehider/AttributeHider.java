package com.kabryxis.attributehider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class AttributeHider extends JavaPlugin {
	
	private Remover remover;
	
	@Override
	public void onEnable() {
		Plugin protocolPlugin = getServer().getPluginManager().getPlugin("ProtocolLib");
		if (protocolPlugin == null) {
			getLogger().severe("AttributeHider requires ProtocolLib to be installed. Disabling.");
			return;
		}
		Set<PacketType> packetTypes = new HashSet<>(PacketType.Play.Server.getInstance().values());
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, packetTypes) {
			
			@Override
			public void onPacketSending(PacketEvent event) {
				getLogger().info(event.getPacketType().toString());
			}
			
		});
		saveDefaultConfig();
		remover = new Remover(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("attributehider")) {
			if (!sender.hasPermission(getConfig().getString("command.permission", "ah.reload"))) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig()
						.getString("command.no-permission", "&6You do not have permission to use this command!")));
				return true;
			}
			reloadConfig();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig()
					.getString("command.reloaded", "&6The AttributeHider configuration has been reloaded.")));
			remover.setup();
			return true;
		}
		return false;
	}
	
}
