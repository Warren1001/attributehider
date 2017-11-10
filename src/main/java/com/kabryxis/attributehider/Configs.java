package com.kabryxis.attributehider;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Configs {
	
	public final static int VERSION = 3;
	
	private final static ConfigTransformer[] transformers = new ConfigTransformer[VERSION - 1];
	
	static {
		// Version 1 to Version 2
		transformers[0] = new ConfigTransformer() {
			
			@Override
			public void transform(FileConfiguration oldConfig, FileConfiguration newConfig) {
				newConfig.set("modify.villagers", oldConfig.get("modify-villagers"));
				oldConfig.set("modify-villagers", null);
				ConfigurationSection list = oldConfig.getConfigurationSection("list"), attributesList = newConfig.getConfigurationSection("lists.attributes");
				attributesList.set("mode", list.get("mode"));
				attributesList.set("list", list.get("list"));
				oldConfig.set("list", null);
				for(Entry<String, Object> entry : oldConfig.getValues(true).entrySet()) {
					newConfig.set(entry.getKey(), entry.getValue());
				}
			}
			
		};
		// Version 2 to Version 3
		transformers[1] = new ConfigTransformer() {
			
			@Override
			public void transform(FileConfiguration oldConfig, FileConfiguration newConfig) {
				oldConfig.set("modify", null);
				newConfig.set("check-updates", oldConfig.get("update.check"));
				oldConfig.set("update", null);
				for(Entry<String, Object> entry : oldConfig.getValues(true).entrySet()) {
					newConfig.set(entry.getKey(), entry.getValue());
				}
			}
			
		};
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
		
		public void transform(FileConfiguration oldConfig, FileConfiguration newConfig);
		
	}
	
}
