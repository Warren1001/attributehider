package com.kabryxis.attributehider;

import com.kabryxis.kabutils.spigot.version.Version;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class AttributeHider extends JavaPlugin {
	
	private Remover remover;
	
	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			disablePlugin("This plugin requires the plugin ProtocolLib to be installed.");
			return;
		}
		if(Version.VERSION == Version.UNKNOWN) {
			disablePlugin("This plugin does not support your Minecraft server version.");
			return;
		}
		switch(Configs.check(this)) {
		case 0:
			message("Successfully updated your config.yml to the latest version. Check it for new values to configure.");
			break;
		case 1:
			// Already up-to-date
			break;
		default:
			message("There was an error updating your config.yml. If this keeps happening, delete your current config.yml and then restart the server to get a new config.yml.");
			break;
		}
		remover = new Remover(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("attributehider")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload")) {
					if(!sender.hasPermission(getConfig().getString("command.permission"))) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.no-permission")));
						return true;
					}
					reloadConfig();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("command.reloaded")));
					remover.setup();
					return true;
				} else if(args[0].equalsIgnoreCase("test")) {
					if(!sender.hasPermission("ah.test")) return false;
					Player player = (Player)sender;
					Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
					villager.setAge(10000);
					villager.setProfession(Villager.Profession.ARMORER);
					return true;
				}
			}
		}
		return false;
	}
	
	public void message(String message) {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[AttributeHider] " + message);
	}
	
	private void disablePlugin(String message) {
		message(message + " Disabling.");
		getServer().getPluginManager().disablePlugin(this);
	}
	
}
