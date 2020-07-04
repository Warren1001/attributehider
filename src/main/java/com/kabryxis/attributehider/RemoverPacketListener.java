package com.kabryxis.attributehider;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.ConstructorAccessor;
import com.comphenix.protocol.reflect.accessors.MethodAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.Converters;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoverPacketListener extends PacketAdapter {
	
	private final Remover remover;
	
	public RemoverPacketListener(Plugin plugin, Remover remover) {
		super(plugin, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.OPEN_WINDOW_MERCHANT);
		this.remover = remover;
	}
	
	@Override
	public void onPacketSending(PacketEvent event) {
		
		PacketContainer packet = event.getPacket();
		
		if (packet.getType() == PacketType.Play.Server.WINDOW_ITEMS) {
			
			if (MinecraftVersion.EXPLORATION_UPDATE.atOrAbove()) { // 1.11
				StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
				List<ItemStack>                    items    = modifier.read(0);
				remover.modify(items);
				modifier.write(0, items);
			} else {
				StructureModifier<ItemStack[]> modifier = packet.getItemArrayModifier();
				ItemStack[]                    items    = modifier.read(0);
				remover.modify(items);
				modifier.write(0, items);
			}
			
		} else if (packet.getType() == PacketType.Play.Server.OPEN_WINDOW_MERCHANT) {
			
			modifyMerchantRecipeList(getMerchantRecipeLists(packet)); // until ProtocolLib adds this functionality to their own plugin
			
		} else { // PacketType.Play.Server.SET_SLOT
			
			StructureModifier<ItemStack> modifier = packet.getItemModifier();
			ItemStack                    item     = modifier.read(0);
			remover.modify(item);
			modifier.write(0, item);
			
		}
		
	}
	
	public void modifyMerchantRecipeList(StructureModifier<List<MerchantRecipe>> modifier) {
		
		List<MerchantRecipe> merchantRecipeList    = modifier.read(0);
		List<MerchantRecipe> newMerchantRecipeList = new ArrayList<>(merchantRecipeList.size());
		
		for (MerchantRecipe recipe : merchantRecipeList) {
			
			MerchantRecipe newRecipe = new MerchantRecipe(remover.modify(recipe.getResult()), recipe.getUses(), recipe.getMaxUses(),
					recipe.hasExperienceReward(), recipe
					.getVillagerExperience(), recipe.getPriceMultiplier());
			newRecipe.setIngredients(recipe.getIngredients().stream().map(remover::modify).collect(Collectors.toList()));
			
			newMerchantRecipeList.add(newRecipe);
		}
		
		modifier.write(0, newMerchantRecipeList);
		
	}
	
	public static StructureModifier<List<MerchantRecipe>> getMerchantRecipeLists(PacketContainer packet) {
		return packet.getModifier().withType(MinecraftReflection.getMinecraftClass("MerchantRecipeList"), getMerchantRecipeListConverter());
	}
	
	private static ConstructorAccessor merchantRecipeListConstructor = null;
	private static MethodAccessor      bukkitMerchantRecipeToCraft   = null;
	private static MethodAccessor      craftMerchantRecipeToNMS      = null;
	private static MethodAccessor      nmsMerchantRecipeToBukkit     = null;
	
	@SuppressWarnings("unchecked")
	public static EquivalentConverter<List<MerchantRecipe>> getMerchantRecipeListConverter() {
		return Converters.ignoreNull(new EquivalentConverter<List<MerchantRecipe>>() {
			
			@Override
			public Object getGeneric(List<MerchantRecipe> specific) {
				if (merchantRecipeListConstructor == null) {
					Class<?> merchantRecipeListClass = MinecraftReflection.getMinecraftClass("MerchantRecipeList");
					merchantRecipeListConstructor = Accessors.getConstructorAccessor(merchantRecipeListClass);
					Class<?>        craftMerchantRecipeClass = MinecraftReflection.getCraftBukkitClass("inventory.CraftMerchantRecipe");
					FuzzyReflection reflection               = FuzzyReflection.fromClass(craftMerchantRecipeClass, false);
					bukkitMerchantRecipeToCraft = Accessors.getMethodAccessor(reflection.getMethodByName("fromBukkit"));
					craftMerchantRecipeToNMS = Accessors.getMethodAccessor(reflection.getMethodByName("toMinecraft"));
				}
				return specific.stream()
				               .map(recipe -> craftMerchantRecipeToNMS.invoke(bukkitMerchantRecipeToCraft.invoke(null, recipe)))
				               .collect(() -> (List<Object>)merchantRecipeListConstructor.invoke(), List::add, List::addAll);
			}
			
			@Override
			public List<MerchantRecipe> getSpecific(Object generic) {
				if (nmsMerchantRecipeToBukkit == null) {
					Class<?>        merchantRecipeClass = MinecraftReflection.getMinecraftClass("MerchantRecipe");
					FuzzyReflection reflection          = FuzzyReflection.fromClass(merchantRecipeClass, false);
					nmsMerchantRecipeToBukkit = Accessors.getMethodAccessor(reflection.getMethodByName("asBukkit"));
				}
				return ((List<Object>)generic).stream().map(o -> (MerchantRecipe)nmsMerchantRecipeToBukkit.invoke(o)).collect(Collectors.toList());
			}
			
			@Override
			public Class<List<MerchantRecipe>> getSpecificType() {
				return ((Class<List<MerchantRecipe>>)((Class<?>)List.class));
			}
			
		});
	}
	
}
