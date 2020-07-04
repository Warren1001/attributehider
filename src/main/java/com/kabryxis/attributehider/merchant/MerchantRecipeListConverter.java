package com.kabryxis.attributehider.merchant;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class MerchantRecipeListConverter implements EquivalentConverter<List<MerchantRecipe>> {
	
	private static ConstructorAccessor MERCHANT_RECIPE_LIST_CONSTRUCTOR = null;
	private static MethodAccessor      MERCHANT_RECIPE_BUKKIT_TO_CRAFT  = null;
	private static MethodAccessor      MERCHANT_RECIPE_CRAFT_TO_NMS     = null;
	private static MethodAccessor      MERCHANT_RECIPE_NMS_TO_BUKKIT    = null;
	
	@Override
	public Object getGeneric(List<MerchantRecipe> specific) {
		
		if (MERCHANT_RECIPE_LIST_CONSTRUCTOR == null) {
			
			FuzzyReflection reflection = FuzzyReflection.fromClass(MinecraftReflection.getCraftBukkitClass("inventory.CraftMerchantRecipe"), false);
			
			MERCHANT_RECIPE_LIST_CONSTRUCTOR = Accessors.getConstructorAccessor(MinecraftReflection.getMinecraftClass("MerchantRecipeList"));
			MERCHANT_RECIPE_BUKKIT_TO_CRAFT = Accessors.getMethodAccessor(reflection.getMethodByName("fromBukkit"));
			MERCHANT_RECIPE_CRAFT_TO_NMS = Accessors.getMethodAccessor(reflection.getMethodByName("toMinecraft"));
			
		}
		return specific.stream()
				.map(recipe -> MERCHANT_RECIPE_CRAFT_TO_NMS.invoke(MERCHANT_RECIPE_BUKKIT_TO_CRAFT.invoke(null, recipe)))
				.collect(() -> (List<Object>)MERCHANT_RECIPE_LIST_CONSTRUCTOR.invoke(), List::add, List::addAll);
	}
	
	@Override
	public List<MerchantRecipe> getSpecific(Object generic) {
		if (MERCHANT_RECIPE_NMS_TO_BUKKIT == null) {
			FuzzyReflection reflection = FuzzyReflection.fromClass(MinecraftReflection.getMinecraftClass("MerchantRecipe"), false);
			MERCHANT_RECIPE_NMS_TO_BUKKIT = Accessors.getMethodAccessor(reflection.getMethodByName("asBukkit"));
		}
		return ((List<Object>)generic).stream().map(o -> (MerchantRecipe)MERCHANT_RECIPE_NMS_TO_BUKKIT.invoke(o)).collect(Collectors.toList());
	}
	
	@Override
	public Class<List<MerchantRecipe>> getSpecificType() {
		return ((Class<List<MerchantRecipe>>)((Class<?>)List.class));
	}
	
}
