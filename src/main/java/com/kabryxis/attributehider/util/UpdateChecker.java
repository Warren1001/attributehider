package com.kabryxis.attributehider.util;

import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class UpdateChecker {
	
	public static void check(Plugin plugin, int resourceId, Consumer<ResponseType> handler) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					
					HttpsURLConnection connection = (HttpsURLConnection)new URL(
							"https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/5.0");
					
					String spigotVersion = Resources.toString(connection.getURL(), Charset.defaultCharset());
					String localVersion = plugin.getDescription().getVersion();
					//plugin.getLogger().info(String.format("spigotVersion=%s,localVersion=%s", spigotVersion, localVersion));
					if (spigotVersion.equalsIgnoreCase(localVersion)) {
						handler.accept(ResponseType.UP_TO_DATE);
					} else {
						handler.accept(ResponseType.NEW_VERSION);
					}
					
				} catch (IOException e) {
					handler.accept(ResponseType.ERROR);
				}
			}
			
		}.runTaskAsynchronously(plugin);
	}
	
	public enum ResponseType {
		UP_TO_DATE, NEW_VERSION, ERROR
	}
	
}
