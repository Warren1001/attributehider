package com.kabryxis.attributehider.remover;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kabryxis.attributehider.AttributeHider;

public abstract class Remover {
	
	public abstract void remove(Villager villager, Player player);
	
	public void remove(ItemStack item) {
		if(!AttributeHider.shouldBeModified(item)) return;
		ItemMeta meta = item.getItemMeta();
		if(meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) return;
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(meta);
	}
	
	public void remove(ItemStack... items) {
		for(ItemStack item : items) {
			remove(item);
		}
	}
	
	public void remove(Collection<? extends ItemStack> items) {
		for(ItemStack item : items) {
			remove(item);
		}
	}
	
}
