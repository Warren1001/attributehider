package com.kabryxis.attributehider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitListener implements Listener {
	
	private final Remover remover;
	
	public BukkitListener(Remover remover) {
		this.remover = remover;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		remover.modify(event.getPlayer().getInventory());
	}
	
}
