package com.kabryxis.attributehider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.nisovin.shopkeepers.events.OpenTradeEvent;
import com.nisovin.shopkeepers.events.ShopkeeperEditedEvent;

public class ShopkeepersListener implements Listener {
	
	private final Remover remover;
	
	public ShopkeepersListener(Remover remover) {
		this.remover = remover;
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onOpenTrade(OpenTradeEvent event) {
		event.getShopkeeper().getRecipes().forEach(remover::modify);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onShopkeeperEdited(ShopkeeperEditedEvent event) {
		event.getShopkeeper().getRecipes().forEach(remover::modify);
	}
	
}
