package com.kabryxis.attributehider;

import com.kabryxis.attributehider.remover.Remover;
import com.kabryxis.attributehider.util.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class AttributeHider extends JavaPlugin {
	
	public static final boolean DEV = false;
	
	private Remover remover;
	
	@Override
	public void onEnable() {
		if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			getLogger().severe("AttributeHider requires ProtocolLib >= 4.6.0 to be installed. Disabling.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		saveDefaultConfig();
		remover = new Remover(this);
		
		checkForUpdates();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("attributehider")) {
			if (!sender.hasPermission(getConfig().getString("command.permission", "ah.reload"))) {
				sendConfigMessage(sender, "command.no-permission", "&6You do not have permission to use this command!");
				return true;
			}
			reloadConfig();
			remover.setup();
			checkForUpdates();
			sendConfigMessage(sender, "command.reloaded", "&6The AttributeHider configuration has been reloaded.");
			return true;
		}
		return false;
	}
	
	private void checkForUpdates() {
		if (getConfig().getBoolean("check-updates", false)) {
			UpdateChecker.check(this, 10604, response -> {
				if (response == UpdateChecker.ResponseType.NEW_VERSION) {
					getLogger().info("There is a new version available!");
				} else if (response == UpdateChecker.ResponseType.ERROR) {
					getLogger().warning("Unable to check for updates.");
				}
			});
		}
	}
	
	private void sendConfigMessage(CommandSender sender, String key, String def) {
		String message = ChatColor.translateAlternateColorCodes('&', getConfig().getString(key, def));
		if (!message.isEmpty()) {
			sender.sendMessage(message);
		}
	}
	
}
