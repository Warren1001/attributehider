package com.kabryxis.attributehider.version.wrapper.merchantrecipelist;

import java.util.List;

import com.kabryxis.attributehider.version.wrapper.Wrappable;
import com.kabryxis.attributehider.version.wrapper.merchantrecipe.WrappedMerchantRecipe;

public abstract class WrappedMerchantRecipeList<T> extends Wrappable<T> {
	
	public abstract List<WrappedMerchantRecipe<?>> getRecipes();
	
	public abstract void setRecipes(List<WrappedMerchantRecipe<?>> recipes);
	
}
