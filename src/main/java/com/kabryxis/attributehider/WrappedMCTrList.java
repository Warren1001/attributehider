package com.kabryxis.attributehider;

import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.google.common.collect.Lists;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class WrappedMCTrList {
	
	private static final Class<?>       NMS_CLASS       = MinecraftReflection.getPacketDataSerializerClass();
	private static final Class<?>       CLASS_ITEMSTACK = MinecraftReflection.getItemStackClass();
	private static final MethodAccessor READ_INT;
	private static final MethodAccessor READ_BYTE;
	private static final MethodAccessor READ_ITEMSTACK;
	private static final MethodAccessor READ_BOOLEAN;
	private static final MethodAccessor WRITE_INT;
	private static final MethodAccessor WRITE_BYTE;
	private static final MethodAccessor WRITE_ITEMSTACK;
	private static final MethodAccessor WRITE_BOOLEAN;
	
	static {
		FuzzyReflection reflection = FuzzyReflection.fromClass(NMS_CLASS, false);
		READ_INT = Accessors.getMethodAccessor(reflection.getMethodByName("readInt"));
		READ_BYTE = Accessors.getMethodAccessor(reflection.getMethodByName("readByte"));
		FuzzyMethodContract readItemStackContract = FuzzyMethodContract.newBuilder().returnTypeExact(CLASS_ITEMSTACK).parameterCount(0).build();
		READ_ITEMSTACK = Accessors.getMethodAccessor(reflection.getMethod(readItemStackContract));
		READ_BOOLEAN = Accessors.getMethodAccessor(reflection.getMethodByName("readBoolean"));
		WRITE_INT = Accessors.getMethodAccessor(reflection.getMethodByName("writeInt"));
		WRITE_BYTE = Accessors.getMethodAccessor(reflection.getMethodByName("writeByte"));
		FuzzyMethodContract writeItemStackContract = FuzzyMethodContract.newBuilder().parameterExactType(CLASS_ITEMSTACK).build();
		WRITE_ITEMSTACK = Accessors.getMethodAccessor(reflection.getMethod(writeItemStackContract));
		WRITE_BOOLEAN = Accessors.getMethodAccessor(reflection.getMethodByName("writeBoolean"));
		
	}
	
	private final Object handle;
	private final int containerCounter;
	
	private List<MerchantRecipe> merchantRecipeList;
	
	public WrappedMCTrList(Object handle) {
		this.handle = handle;
		containerCounter = (int)READ_INT.invoke(handle);
		int size = (int)READ_BYTE.invoke(handle);
		merchantRecipeList = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			List<ItemStack> ingredients = Lists.newArrayList(MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle)));
			ItemStack result = MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle));
			boolean hasSecondIngredient = (boolean)READ_BOOLEAN.invoke(handle);
			if(hasSecondIngredient) ingredients.add(MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle)));
			boolean rewardsExp = (boolean)READ_BOOLEAN.invoke(handle);
			int uses = (int)READ_INT.invoke(handle);
			int maxUses = (int)READ_INT.invoke(handle);
			MerchantRecipe recipe = new MerchantRecipe(result, uses, maxUses, rewardsExp);
			recipe.setIngredients(ingredients);
			merchantRecipeList.add(recipe);
		}
	}
	
	public List<MerchantRecipe> getMerchantRecipeList() {
		return merchantRecipeList;
	}
	
	public void setMerchantRecipeList(List<MerchantRecipe> merchantRecipeList) {
		this.merchantRecipeList = merchantRecipeList;
	}
	
	public Object convertToPacketDataSerializer() {
		Object packetDataSerializer =
	}
	
}
