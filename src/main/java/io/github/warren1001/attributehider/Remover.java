package io.github.warren1001.attributehider;

import java.lang.reflect.Field;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Remover {
	
	protected final Main plugin;
	protected final Field field;
	
	public Remover(Main plugin, Field field) {
		this.plugin = plugin;
		this.field = field;
	}
	
	public abstract void remove(Player player);
	
	public void remove(final ItemStack... items) {
		for(ItemStack item : items) {
			if(!plugin.shouldBeModified(item)) continue;
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(meta);
		}
	}
	

}
