package com.kabryxis.attributehider.merchant;

import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.reflect.fuzzy.FuzzyMethodContract;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class MCTrList {
	
	private static MethodAccessor READ_INT;
	private static MethodAccessor READ_BYTE;
	private static MethodAccessor READ_ITEMSTACK;
	private static MethodAccessor READ_BOOLEAN;
	private static MethodAccessor BUFFER;
	private static MethodAccessor WRITE_INT;
	private static MethodAccessor WRITE_BYTE;
	private static MethodAccessor WRITE_ITEMSTACK;
	private static MethodAccessor WRITE_BOOLEAN;
	
	private final int containerCounter;
	
	private List<MerchantRecipe> merchantRecipeList;
	
	public MCTrList(Object handle) {
		
		if (READ_INT == null) {
			
			FuzzyReflection reflection = FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), false);
			FuzzyMethodContract readItemStackContract = FuzzyMethodContract.newBuilder()
					.returnTypeExact(MinecraftReflection.getItemStackClass())
					.parameterCount(0)
					.build();
			
			READ_INT = Accessors.getMethodAccessor(reflection.getMethodByName("readInt"));
			READ_BYTE = Accessors.getMethodAccessor(reflection.getMethodByName("readByte"));
			READ_ITEMSTACK = Accessors.getMethodAccessor(reflection.getMethod(readItemStackContract));
			READ_BOOLEAN = Accessors.getMethodAccessor(reflection.getMethodByName("readBoolean"));
			
		}
		
		containerCounter = (int)READ_INT.invoke(handle);
		
		int size = (((byte)READ_BYTE.invoke(handle)) & 0xFF);
		merchantRecipeList = new ArrayList<>(size);
		
		for (int i = 0; i < size; i++) {
			
			ItemStack buyItem1 = MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle));
			ItemStack buyItem2 = null;
			ItemStack result   = MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle));
			
			boolean hasSecondItem = (boolean)READ_BOOLEAN.invoke(handle);
			if (hasSecondItem) buyItem2 = MinecraftReflection.getBukkitItemStack(READ_ITEMSTACK.invoke(handle));
			
			boolean rewardsExp = (boolean)READ_BOOLEAN.invoke(handle);
			int     uses       = (int)READ_INT.invoke(handle);
			int     maxUses    = (int)READ_INT.invoke(handle);
			
			merchantRecipeList.add(new MerchantRecipe(buyItem1, buyItem2, result, uses, maxUses, rewardsExp));
			
		}
		
	}
	
	public List<MerchantRecipe> getMerchantRecipeList() {
		return merchantRecipeList;
	}
	
	public void setMerchantRecipeList(List<MerchantRecipe> merchantRecipeList) {
		this.merchantRecipeList = merchantRecipeList;
	}
	
	public Object convertToPacketDataSerializer() {
		
		if (BUFFER == null) {
			
			FuzzyReflection reflection = FuzzyReflection.fromClass(MinecraftReflection.getPacketDataSerializerClass(), false);
			FuzzyMethodContract bufferContract = FuzzyMethodContract.newBuilder()
					.parameterCount(0)
					.returnTypeExact(MinecraftReflection.getByteBufClass())
					.requireModifier(Modifier.STATIC)
					.nameExact("buffer")
					.build();
			FuzzyMethodContract writeItemStackContract = FuzzyMethodContract.newBuilder()
					.parameterExactType(MinecraftReflection.getItemStackClass())
					.build();
			
			BUFFER = Accessors.getMethodAccessor(
					FuzzyReflection.fromClass(MinecraftReflection.getMinecraftLibraryClass("io.netty.buffer.Unpooled")).getMethod(bufferContract));
			WRITE_INT = Accessors.getMethodAccessor(reflection.getMethodByName("writeInt"));
			WRITE_BYTE = Accessors.getMethodAccessor(reflection.getMethodByName("writeByte"));
			WRITE_ITEMSTACK = Accessors.getMethodAccessor(reflection.getMethod(writeItemStackContract));
			WRITE_BOOLEAN = Accessors.getMethodAccessor(reflection.getMethodByName("writeBoolean"));
			
		}
		
		Object packetDataSerializerObject = MinecraftReflection.getPacketDataSerializer(BUFFER.invoke(null));
		
		WRITE_INT.invoke(packetDataSerializerObject, containerCounter);
		WRITE_BYTE.invoke(packetDataSerializerObject, (byte)(merchantRecipeList.size() & 0xFF));
		
		for (MerchantRecipe recipe : merchantRecipeList) {
			
			WRITE_ITEMSTACK.invoke(packetDataSerializerObject, MinecraftReflection.getMinecraftItemStack(recipe.getBuyItem1()));
			WRITE_ITEMSTACK.invoke(packetDataSerializerObject, MinecraftReflection.getMinecraftItemStack(recipe.getResult()));
			
			boolean hasBuyItem2 = recipe.hasBuyItem2();
			WRITE_BOOLEAN.invoke(packetDataSerializerObject, hasBuyItem2);
			if (hasBuyItem2) WRITE_ITEMSTACK.invoke(packetDataSerializerObject, MinecraftReflection.getMinecraftItemStack(recipe.getBuyItem2()));
			
			WRITE_BOOLEAN.invoke(packetDataSerializerObject, recipe.doesRewardExperience());
			WRITE_INT.invoke(packetDataSerializerObject, recipe.getUses());
			WRITE_INT.invoke(packetDataSerializerObject, recipe.getMaxUses());
			
		}
		
		return packetDataSerializerObject;
	}
	
}
