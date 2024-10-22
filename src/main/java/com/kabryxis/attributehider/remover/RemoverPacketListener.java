package com.kabryxis.attributehider.remover;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.kabryxis.attributehider.AttributeHider;
import com.kabryxis.attributehider.merchant.MCTrList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoverPacketListener extends PacketAdapter {
	
	private final Remover remover;
	
	public RemoverPacketListener(Plugin plugin, Remover remover, Set<PacketType> packetTypes) {
		super(plugin, packetTypes);
		this.remover = remover;
	}
	
	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		
		if (AttributeHider.DEV) {
			printType(packet.getType());
		}
		
		if (packet.getType() == PacketType.Play.Server.WINDOW_ITEMS) {
			
			if (MinecraftVersion.EXPLORATION_UPDATE.atOrAbove()) { // 1.11
				StructureModifier<List<ItemStack>> modifier = packet.getItemListModifier();
				modifier.write(0, remover.modify(modifier.read(0)));
			} else {
				StructureModifier<ItemStack[]> modifier = packet.getItemArrayModifier();
				modifier.write(0, remover.modify(modifier.read(0)));
			}
			
		} else if (packet.getType() == PacketType.Play.Server.OPEN_WINDOW_MERCHANT) { // post 1.14 packet for Merchants
			
			modifyMerchantRecipeList(packet.getMerchantRecipeLists());
			
		} else if (packet.getType() == PacketType.Play.Server.CUSTOM_PAYLOAD) { // pre 1.14 packet for Merchants
			
			if (MinecraftVersion.AQUATIC_UPDATE.atOrAbove()) { // Update 1.13 added MinecraftKey instead of String for payload ID.
				if (packet.getMinecraftKeys().read(0).getKey().equals("trader_list")) {
					modifyMerchantRecipeListLegacy(getPacketDataSerializers(packet));
				}
			} else if (packet.getStrings().read(0).equals("MC|TrList")) {
				modifyMerchantRecipeListLegacy(getPacketDataSerializers(packet));
			}
			
		} else if (packet.getType() == PacketType.Play.Server.SET_SLOT) {
			
			StructureModifier<ItemStack> modifier = packet.getItemModifier();
			modifier.write(0, remover.modify(modifier.read(0)));
			
		} else if (!AttributeHider.DEV) {
			
			plugin.getLogger().severe(String.format("Unhandled packet type '%s', this should not happen!", packet.getType().name()));
			
		}
		
	}
	
	public void modifyMerchantRecipeList(StructureModifier<List<MerchantRecipe>> modifier) {
		
		List<MerchantRecipe> merchantRecipeList    = modifier.read(0);
		List<MerchantRecipe> newMerchantRecipeList = new ArrayList<>(merchantRecipeList.size());
		
		for (MerchantRecipe recipe : merchantRecipeList) {
			
			MerchantRecipe newRecipe = new MerchantRecipe(remover.modify(recipe.getResult()), recipe.getUses(), recipe.getMaxUses(),
					recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier());
			newRecipe.setIngredients(recipe.getIngredients().stream().map(remover::modify).collect(Collectors.toList()));
			
			newMerchantRecipeList.add(newRecipe);
		}
		
		modifier.write(0, newMerchantRecipeList);
		
	}
	
	public void modifyMerchantRecipeListLegacy(StructureModifier<Object> modifier) {
		
		MCTrList tradeList = new MCTrList(modifier.read(0));
		
		for (com.kabryxis.attributehider.merchant.MerchantRecipe recipe : tradeList.getMerchantRecipeList()) {
			recipe.setBuyItem1(remover.modify(recipe.getBuyItem1()));
			if (recipe.hasBuyItem2()) {
				recipe.setBuyItem2(remover.modify(recipe.getBuyItem2()));
			}
			recipe.setResult(remover.modify(recipe.getResult()));
		}
		
		modifier.write(0, tradeList.convertToPacketDataSerializer());
		
	}
	
	public static StructureModifier<Object> getPacketDataSerializers(PacketContainer packet) {
		return packet.getModifier().withType(MinecraftReflection.getMinecraftClass("PacketDataSerializer"));
	}
	
	/*private final Set<PacketType> DEV_IGNORE = Sets.newHashSet(
			PacketType.Play.Server.REL_ENTITY_MOVE,
			PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
			PacketType.Play.Server.UPDATE_TIME,
			PacketType.Play.Server.ENTITY_VELOCITY,
			PacketType.Play.Server.ENTITY_HEAD_ROTATION,
			PacketType.Play.Server.BLOCK_CHANGE,
			PacketType.Play.Server.MAP_CHUNK,
			PacketType.Play.Server.MULTI_BLOCK_CHANGE,
			PacketType.Play.Server.KEEP_ALIVE,
			PacketType.Play.Server.CHUNK_BATCH_START,
			PacketType.Play.Server.CHUNK_BATCH_FINISHED,
			PacketType.Play.Server.ADVANCEMENTS,
			PacketType.Play.Server.UPDATE_HEALTH,
			PacketType.Play.Server.POSITION,
			PacketType.Play.Server.SYSTEM_CHAT,
			PacketType.Play.Server.INITIALIZE_BORDER,
			PacketType.Play.Server.SPAWN_POSITION,
			PacketType.Play.Server.ENTITY_LOOK,
			PacketType.Play.Server.NAMED_SOUND_EFFECT,
			PacketType.Play.Server.PLAYER_INFO
	);*/
	
	private void printType(PacketType type) {
		/*if (!DEV_IGNORE.contains(type)) {
			plugin.getLogger().info(type.name());
		}*/
	}
	
}
