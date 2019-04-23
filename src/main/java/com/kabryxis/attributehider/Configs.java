package com.kabryxis.attributehider;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Configs {
	
	public final static int VERSION = 4;
	
	private final static ConfigTransformer[] transformers = new ConfigTransformer[VERSION - 1];
	
	static {
		// Version 1 to Version 2
		transformers[0] = (oldConfig, newConfig) -> {
			newConfig.set("modify.villagers", oldConfig.get("modify-villagers"));
			oldConfig.set("modify-villagers", null);
			ConfigurationSection list = oldConfig.getConfigurationSection("list"), attributesList = newConfig.getConfigurationSection("lists.attributes");
			attributesList.set("mode", list.get("mode"));
			attributesList.set("list", list.get("list"));
			oldConfig.set("list", null);
		};
		// Version 2 to Version 3
		transformers[1] = (oldConfig, newConfig) -> {
			oldConfig.set("modify", null);
			newConfig.set("check-updates", oldConfig.get("update.check"));
			oldConfig.set("update", null);
		};
		// Version 3 to Version 4
		transformers[2] = (oldConfig, newConfig) -> {};
	}
	
	public static int check(Plugin plugin) {
		File file = new File(plugin.getDataFolder(), "config.yml");
		if(!file.exists()) {
			plugin.saveDefaultConfig();
			return 1;
		}
		int current = plugin.getConfig().getInt("version");
		if(current < VERSION) {
			InputStreamReader isr = new InputStreamReader(plugin.getResource("config.yml"));
			FileConfiguration newConfig = YamlConfiguration.loadConfiguration(isr), oldConfig = plugin.getConfig();
			for(int i = current - 1; i <= VERSION - 2; i++) {
				transformers[i].transform(oldConfig, newConfig);
			}
			oldConfig.getValues(true).entrySet().stream().filter(e -> !(e.getValue() instanceof ConfigurationSection))
					.forEach(entry -> newConfig.set(entry.getKey(), entry.getValue()));
			newConfig.set("version", VERSION);
			try {
				isr.close();
				newConfig.save(file);
				plugin.reloadConfig();
				return 0;
			}
			catch(IOException e) {
				e.printStackTrace();
				return 2;
			}
		}
		return 1;
	}
	
	public interface ConfigTransformer {
		
		void transform(FileConfiguration oldConfig, FileConfiguration newConfig);
		
	}
	
}
