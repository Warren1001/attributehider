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
import java.util.regex.Pattern;

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
					
					String spigotVersion = Resources.toString(connection.getURL(), Charset.defaultCharset()).trim();
					String localVersion = plugin.getDescription().getVersion().trim();
					//plugin.getLogger().info(String.format("spigotVersion=%s,localVersion=%s", spigotVersion, localVersion));
					if (isNewer(convertToIntArray(localVersion), convertToIntArray(spigotVersion))) {
						handler.accept(ResponseType.NEW_VERSION);
					} else {
						handler.accept(ResponseType.UP_TO_DATE);
					}
					
				} catch (IOException e) {
					handler.accept(ResponseType.ERROR);
				}
			}
			
		}.runTaskAsynchronously(plugin);
	}
	
	public static int[] convertToIntArray(String version) {
		version = version.trim();
		String[] args = version.split(Pattern.quote("."));
		int[] versions = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			versions[i] = Integer.parseInt(args[i]);
		}
		return versions;
	}
	
	public static boolean isNewer(int[] currentVersions, int[] serverVersions) {
		for (int i = 0; i < Math.min(currentVersions.length, serverVersions.length); i++) {
			if (serverVersions[i] > currentVersions[i]) return true;
		}
		return false;
	}
	
	public enum ResponseType {
		UP_TO_DATE, NEW_VERSION, ERROR
	}
	
}
