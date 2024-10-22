package com.kabryxis.attributehider.remover.list;

import org.bukkit.Material;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class EnumSetMaterialList implements MaterialList {
	
	private final Set<Material> enumSet;
	
	public EnumSetMaterialList(Collection<Material> list) {
		enumSet = EnumSet.copyOf(list);
	}
	
	@Override
	public boolean shouldRemove(Material type) {
		return enumSet.contains(type);
	}
	
}
