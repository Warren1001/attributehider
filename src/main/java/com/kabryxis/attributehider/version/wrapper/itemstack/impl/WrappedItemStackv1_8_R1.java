package com.kabryxis.attributehider.version.wrapper.itemstack.impl;

import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;

import com.kabryxis.attributehider.version.wrapper.itemstack.WrappedItemStack;

import net.minecraft.server.v1_8_R1.ItemStack;

public class WrappedItemStackv1_8_R1 extends WrappedItemStack<ItemStack> {
	
	@Override
	public void setBukkitItemStack(org.bukkit.inventory.ItemStack item) {
		set(CraftItemStack.asNMSCopy(item));
	}
	
	@Override
	public org.bukkit.inventory.ItemStack getBukkitItemStack() {
		return CraftItemStack.asBukkitCopy(object);
	}
	
}
