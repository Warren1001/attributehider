package com.kabryxis.attributehider;

import com.nisovin.shopkeepers.api.events.ShopkeeperEditedEvent;
import com.nisovin.shopkeepers.api.events.ShopkeeperTradeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ShopkeepersListener implements Listener {
	
	private final Remover remover;
	
	public ShopkeepersListener(Remover remover) {
		this.remover = remover;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShopkeeperTrade(ShopkeeperTradeEvent event) {
		event.getShopkeeper().getTradingRecipes(event.getPlayer()).forEach(r -> {
			remover.modify(r.getItem1());
			remover.modify(r.getItem2());
			remover.modify(r.getResultItem());
		});
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onShopkeeperEdited(ShopkeeperEditedEvent event) {
		event.getShopkeeper().getTradingRecipes(event.getPlayer()).forEach(r -> {
			remover.modify(r.getItem1());
			remover.modify(r.getItem2());
			remover.modify(r.getResultItem());
		});
	}
	
}
