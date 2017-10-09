package com.kabryxis.attributehider.version.wrapper.merchantrecipe;

import com.kabryxis.attributehider.version.wrapper.Wrappable;

public abstract class WrappedMerchantRecipe<T> extends Wrappable<T> {
	
	public abstract void newInstance(Object buyItem1, Object buyItem2, Object buyItem3, int uses, int maxUses);
	
	public abstract Object handleGetBuyingItem1();
	
	public abstract Object handleGetBuyingItem2();
	
	public abstract boolean hasSecondItem();
	
	public abstract Object handleGetSellingItem();
	
	public abstract int getUses();
	
	public abstract int getMaxUses();
	
}
