package com.kabryxis.attributehider.version.wrapper.merchantrecipelist.impl;

import java.util.ArrayList;
import java.util.List;

import com.kabryxis.attributehider.version.wrapper.WrapperCache;
import com.kabryxis.attributehider.version.wrapper.merchantrecipe.WrappedMerchantRecipe;
import com.kabryxis.attributehider.version.wrapper.merchantrecipe.impl.WrappedMerchantRecipev1_8_R3;
import com.kabryxis.attributehider.version.wrapper.merchantrecipelist.WrappedMerchantRecipeList;

import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;

public class WrappedMerchantRecipeListv1_8_R3 extends WrappedMerchantRecipeList<MerchantRecipeList> {
	
	@Override
	public List<WrappedMerchantRecipe<?>> getRecipes() {
		List<WrappedMerchantRecipe<?>> list = new ArrayList<>(object.size());
		for(Object obj : object) {
			MerchantRecipe recipe = (MerchantRecipe)obj;
			WrappedMerchantRecipev1_8_R3 handle = (WrappedMerchantRecipev1_8_R3)WrapperCache.get(WrappedMerchantRecipe.class);
			handle.set(recipe);
			list.add(handle);
		}
		return list;
	}
	
	@Override
	public void setRecipes(List<WrappedMerchantRecipe<?>> recipes) {
		object.clear();
		recipes.forEach(r -> object.add(((WrappedMerchantRecipev1_8_R3)r).get()));
	}
	
}
