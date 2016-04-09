package io.github.warren1001.attributehider;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class Main extends JavaPlugin {

	private Remover remover;

	@Override
	public void onEnable() {
		if(!setup()) return;
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.CUSTOM_PAYLOAD, PacketType.Play.Server.SET_SLOT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				PacketType type = packet.getType();
				if(type == PacketType.Play.Server.WINDOW_ITEMS) remover.remove(packet.getItemArrayModifier().read(0));
				else if(type == PacketType.Play.Server.SET_SLOT) remover.remove(packet.getItemModifier().read(0));
				else if(packet.getStrings().read(0).equals("MC|TrList")) remover.remove(event.getPlayer());
			}
		});
	}
	
	private boolean setup() {
		String version = getServer().getClass().getPackage().getName();
		try {
			remover = (Remover)Class.forName("io.github.warren1001.attributehider.Remover" + version.substring(version.lastIndexOf('.') + 2).replace("R", "")).newInstance();
			return true;
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[AttributeHider] This plugin does not support your Minecraft server version. Disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return false;
		}
	}
	
}
