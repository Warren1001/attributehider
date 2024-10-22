package com.kabryxis.attributehider.merchant;

import org.bukkit.inventory.ItemStack;

/*
 * Spigot 1.8.8 does not have MerchantRecipe object
 */
public class MerchantRecipe {
	
	private ItemStack buyItem1;
	private ItemStack buyItem2;
	private ItemStack result;
	private int       uses;
	private int       maxUses;
	private boolean   rewardsExperience;
	
	public MerchantRecipe(ItemStack buyItem1, ItemStack buyItem2, ItemStack result, int uses, int maxUses, boolean rewardsExperience) {
		this.buyItem1 = buyItem1;
		this.buyItem2 = buyItem2;
		this.result = result;
		this.uses = uses;
		this.maxUses = maxUses;
		this.rewardsExperience = rewardsExperience;
	}
	
	public void setBuyItem1(ItemStack buyItem1) {
		this.buyItem1 = buyItem1;
	}
	
	public ItemStack getBuyItem1() {
		return buyItem1;
	}
	
	public boolean hasBuyItem2() {
		return buyItem2 != null;
	}
	
	public ItemStack getBuyItem2() {
		return buyItem2;
	}
	
	public void setBuyItem2(ItemStack buyItem2) {
		this.buyItem2 = buyItem2;
	}
	
	public ItemStack getResult() {
		return result;
	}
	
	public void setResult(ItemStack result) {
		this.result = result;
	}
	
	public int getUses() {
		return uses;
	}
	
	public void setUses(int uses) {
		this.uses = uses;
	}
	
	public int getMaxUses() {
		return maxUses;
	}
	
	public void setMaxUses(int maxUses) {
		this.maxUses = maxUses;
	}
	
	public boolean doesRewardExperience() {
		return rewardsExperience;
	}
	
	public void setRewardsExperience(boolean rewardsExperience) {
		this.rewardsExperience = rewardsExperience;
	}
	
}
