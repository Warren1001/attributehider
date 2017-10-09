package com.kabryxis.attributehider.version.wrapper.entityvillager.impl;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftVillager;
import org.bukkit.entity.Villager;

import com.kabryxis.attributehider.version.wrapper.WrapperCache;
import com.kabryxis.attributehider.version.wrapper.entityvillager.WrappedEntityVillager;
import com.kabryxis.attributehider.version.wrapper.merchantrecipelist.WrappedMerchantRecipeList;
import com.kabryxis.attributehider.version.wrapper.merchantrecipelist.impl.WrappedMerchantRecipeListv1_9_R2;

import net.minecraft.server.v1_9_R2.EntityVillager;
import net.minecraft.server.v1_9_R2.NBTTagCompound;

public class WrappedEntityVillagerv1_9_R2 extends WrappedEntityVillager<EntityVillager> {
	
	@Override
	public void setVillager(Villager villager) {
		this.object = ((CraftVillager)villager).getHandle();
	}
	
	@Override
	public WrappedMerchantRecipeListv1_9_R2 getOffers() {
		WrappedMerchantRecipeListv1_9_R2 handle = (WrappedMerchantRecipeListv1_9_R2)WrapperCache.get(WrappedMerchantRecipeList.class);
		handle.set(object.getOffers(null));
		return handle;
	}
	
	@Override
	public void setOffers(WrappedMerchantRecipeList<?> list) {
		NBTTagCompound tag = new NBTTagCompound();
		object.b(tag);
		tag.set("Offers", ((WrappedMerchantRecipeListv1_9_R2)list).get().a());
		object.a(tag);
	}
	
}
