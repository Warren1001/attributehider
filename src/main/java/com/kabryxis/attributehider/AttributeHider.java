package com.kabryxis.attributehider;

import com.kabryxis.attributehider.remover.Remover;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AttributeHider extends JavaPlugin {
	
	private Remover remover;
	
	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			getLogger().severe("AttributeHider requires ProtocolLib to be installed. Disabling.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		saveDefaultConfig();
		remover = new Remover(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("attributehider")) {
			if (!sender.hasPermission(getConfig().getString("command.permission", "ah.reload"))) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						getConfig().getString("command.no-permission", "&6You do not have permission to use this command!")));
				return true;
			}
			reloadConfig();
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					getConfig().getString("command.reloaded", "&6The AttributeHider configuration has been reloaded.")));
			remover.setup();
			return true;
		}
		return false;
	}
	
}
