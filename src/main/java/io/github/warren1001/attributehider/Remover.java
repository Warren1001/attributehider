package io.github.warren1001.attributehider;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Remover {
	
	public abstract void remove(Player player);
	
	public void remove(final ItemStack... items) {
		for(ItemStack item : items) {
			if(item == null || item.getType() == Material.BOOK_AND_QUILL) continue;
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
		}
	}
	
}
