package com.kabryxis.attributehider.remover.impl;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import com.kabryxis.attributehider.AttributeHider;
import com.kabryxis.attributehider.remover.Remover;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class Remover1_8_3 extends Remover {
	
	public Remover1_8_3(AttributeHider plugin, Field field) {
		super(plugin, field);
	}
	
	@Override
	public void remove(Villager villager, Player p) {
		EntityPlayer player = ((CraftPlayer)p).getHandle();
		for(MerchantRecipe recipe : ((CraftVillager)villager).getHandle().getOffers(player)) {
			remove(recipe.getBuyItem1(), recipe.getBuyItem2(), recipe.getBuyItem3());
		}
	}
	
	private void remove(ItemStack... items) {
		for(ItemStack item : items) {
			if(item == null || !plugin.shouldBeModified(Item.getId(item.getItem()))) continue;
			NBTTagCompound tag = item.hasTag() ? item.getTag() : new NBTTagCompound();
			tag.set("AttributeModifiers", new NBTTagList());
			item.setTag(tag);
		}
	}
	
}
