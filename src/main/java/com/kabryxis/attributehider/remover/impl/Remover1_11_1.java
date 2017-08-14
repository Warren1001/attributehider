package com.kabryxis.attributehider.remover.impl;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import com.kabryxis.attributehider.AttributeHider;
import com.kabryxis.attributehider.remover.Remover;

import net.minecraft.server.v1_11_R1.Item;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;

public class Remover1_11_1 extends Remover {
	
	@Override
	public void remove(Villager villager, Player player) {
		((CraftVillager)villager).getHandle().getOffers(((CraftPlayer)player).getHandle()).forEach(recipe -> remove(recipe.getBuyItem1(), recipe.getBuyItem2(), recipe.getBuyItem3()));
	}
	
	private void remove(ItemStack... items) {
		for(ItemStack item : items) {
			remove(item);
		}
	}
	
	private void remove(ItemStack item) {
		if(item == null || !AttributeHider.shouldBeModified(Item.getId(item.getItem()))) return;
		NBTTagCompound tag = item.hasTag() ? item.getTag() : new NBTTagCompound();
		tag.set("AttributeModifiers", new NBTTagList());
		item.setTag(tag);
	}
	
}
