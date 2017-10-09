package com.kabryxis.attributehider.version.wrapper.entityvillager;

import org.bukkit.entity.Villager;

import com.kabryxis.attributehider.version.wrapper.Wrappable;
import com.kabryxis.attributehider.version.wrapper.merchantrecipelist.WrappedMerchantRecipeList;

public abstract class WrappedEntityVillager<T> extends Wrappable<T> {
	
	public abstract void setVillager(Villager villager);
	
	public abstract WrappedMerchantRecipeList<?> getOffers();
	
	public abstract void setOffers(WrappedMerchantRecipeList<?> list);
	
}
