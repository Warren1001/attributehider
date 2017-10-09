package com.kabryxis.attributehider.version.wrapper.itemstack;

import org.bukkit.inventory.ItemStack;

import com.kabryxis.attributehider.version.wrapper.Wrappable;

public abstract class WrappedItemStack<T> extends Wrappable<T> {
	
	public abstract void setBukkitItemStack(ItemStack item);
	
	public abstract ItemStack getBukkitItemStack();
	
}
